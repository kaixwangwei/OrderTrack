# -*- coding: utf-8 -*-
import functools
import json
import os
import random
import time
import datetime
from flask_sqlalchemy import SQLAlchemy
import flask_login
from flask_migrate import Migrate,MigrateCommand
from flask_script import Manager
from flask import Flask, redirect, url_for, request, render_template, make_response, abort, jsonify, \
    send_from_directory
from flask_login import LoginManager
from flask_restful import Api
from flask_uploads import UploadSet, configure_uploads
from gevent import pywsgi

from models.Base import db
from models.LogisticsInfo import LogisticsInfo

import OrderTrackDB
import OrderTrackLogger
from OrderTrackAdmin import admin
from ClientApi import client
from OrderTrackResource import ordertrack_resource
from OrderTrackOperation import operation
from Owner import owner
import OrderConfig
from KuaiDiCX.ShipperMap import SHIPPER_MAP, getLogisticalState
from Index import index
from Login import login
from User import User
from SyncLogistics import *


app = Flask(__name__)
login_manager = LoginManager()
login_manager.init_app(app)
login_manager.login_view = '/login'
# login_manager.login_message = 'please login!'
login_manager.session_protection = 'strong'
logger = OrderTrackLogger.get_logger(__name__)

app.config.from_object(OrderConfig)
db.init_app(app)
with app.test_request_context():
    db.create_all()

uploaded_photos = UploadSet()
configure_uploads(app, uploaded_photos)
api = Api(app)

app.register_blueprint(ordertrack_resource)

app.register_blueprint(admin)

app.register_blueprint(client)

app.register_blueprint(operation)

app.register_blueprint(owner)

app.register_blueprint(index)

app.register_blueprint(login)

#注册蓝图的时候加前缀
#app.register_blueprint(login, url_prefix='/v1')


def setUser(username):
    user = User()
    print("setUser username:%s"%(username))
    tmpUser = OrderTrackDB.get_user_for_login(username)
    if tmpUser == None:
        return None
    user.id = username
    
    if tmpUser.fullname == None:
        user.fullname = tmpUser.username
    else:
        user.fullname = tmpUser.fullname
    user.role = tmpUser.role
    print("[setUser]user.fullname = %s, user.role = %s"%(user.fullname, user.role))
    return user

@login_manager.user_loader
def user_loader(username):
    user = setUser(username)
    if user != None:
        logger.debug("user_loader user is %s, is_authenticated %s" % (user.id, user.is_authenticated))
    
    return user


# 使用request_loader的自定义登录, 同时支持url参数和和使用Authorization头部的基础认证的登录：
@login_manager.request_loader
def request_loader(req):
    logger.debug("request_loader url is %s, request args is %s" % (req.url, req.args))
    authorization = request.headers.get('Authorization')
    logger.debug("Authorization is %s" % authorization)
    # 模拟api登录
    if authorization:
        # get user from authorization
        user = User()
        user.id = 'admin'
        logger.debug("user is %s" % user)
        return user
    return None


@app.route('/error')
@app.errorhandler(400)
@app.errorhandler(401)
@app.errorhandler(500)
def error(e):
    logger.debug("error occurred: %s" % e)
    try:
        code = e.code
        if code == 400:
            return render_template('400.html')
        elif code == 401:
            return render_template('401.html')
        else:
            return render_template('error.html')
    except Exception as e:
        logger.debug('exception is %s' % e)
        return render_template('error.html')


# 文件下载
@app.route('/download/<path:filename>')
def send_html(filename):
    logger.debug("download file, path is %s" % filename)
    return send_from_directory(app.config['UPLOAD_PATH'], filename, as_attachment=True)


@app.route('/api/list', methods=['GET'])
def get_list():
    page = request.args.get('page')
    limit = request.args.get('limit')
    logger.debug("get_list: page = %s, limit = %s" % (page, limit))
    pages = util.get_flask_sqlalchemy(int(page), int(limit))
    if pages is None:
        return jsonify({"code": 0, "msg": "", "count": 0, "data": {}})
    else:
        data = []
        for item in pages.items:
            item.__dict__['_sa_instance_state'] = ''
            item.__dict__['create_time'] = "%s" % item.__dict__['create_time']
            item.__dict__['time'] = "%s" % item.__dict__['time']
            data.append(item.__dict__)
        result = json.dumps({"code": 0, "msg": "", "count": pages.total, "data": data})
        return result


# http://flask-uploads.readthedocs.io/en/latest/
@app.route('/flask-upload', methods=['POST'])
def flask_upload():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            logger.debug('No file part')
            return jsonify({'code': -1, 'filename': '', 'msg': 'No file part'})
        file = request.files['file']
        # if user does not select file, browser also submit a empty part without filename
        if file.filename == '':
            logger.debug('No selected file')
            return jsonify({'code': -1, 'filename': '', 'msg': 'No selected file'})
        else:
            try:
                filename = uploaded_photos.save(file)
                logger.debug('%s url is %s' % (filename, uploaded_photos.url(filename)))
                return jsonify({'code': 0, 'filename': filename, 'msg': uploaded_photos.url(filename)})
            except Exception as e:
                logger.debug('upload file exception: %s' % e)
                return jsonify({'code': -1, 'filename': '', 'msg': 'Error occurred'})
    else:
        return jsonify({'code': -1, 'filename': '', 'msg': 'Method not allowed'})


# show photo
@app.route('/files/<string:filename>', methods=['GET'])
def show_photo(filename):
    if request.method == 'GET':
        if filename is None:
            pass
        else:
            logger.debug('filename is %s' % filename)
            image_data = open(os.path.join(app.config['UPLOAD_PATH'], 'files/%s' % filename), "rb").read()
            response = make_response(image_data)
            response.headers['Content-Type'] = 'image/png'
            return response
    else:
        pass


# http://flask.pocoo.org/docs/0.12/patterns/fileuploads/
@app.route('/upload', methods=['POST'])
def upload_file():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            logger.debug('No file part')
            return jsonify({'code': -1, 'filename': '', 'msg': 'No file part'})
        file = request.files['file']
        # if user does not select file, browser also submit a empty part without filename
        if file.filename == '':
            logger.debug('No selected file')
            return jsonify({'code': -1, 'filename': '', 'msg': 'No selected file'})
        else:
            try:
                if file and allowed_file(file.filename):
                    origin_file_name = file.filename
                    logger.debug('filename is %s' % origin_file_name)
                    # filename = secure_filename(file.filename)
                    filename = origin_file_name

                    if os.path.exists(app.config['UPLOAD_PATH']):
                        logger.debug('%s path exist' % app.config['UPLOAD_PATH'])
                        pass
                    else:
                        logger.debug('%s path not exist, do make dir' % app.config['UPLOAD_PATH'])
                        os.makedirs(app.config['PLOAD_PATH'])

                    file.save(os.path.join(app.config['UPLOAD_PATH'], filename))
                    logger.debug('%s save successfully' % filename)
                    return jsonify({'code': 0, 'filename': origin_file_name, 'msg': ''})
                else:
                    logger.debug('%s not allowed' % file.filename)
                    return jsonify({'code': -1, 'filename': '', 'msg': 'File not allowed'})
            except Exception as e:
                logger.debug('upload file exception: %s' % e)
                return jsonify({'code': -1, 'filename': '', 'msg': 'Error occurred'})
    else:
        return jsonify({'code': -1, 'filename': '', 'msg': 'Method not allowed'})


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in app.config['ALLOWED_EXTENSIONS']


@app.route('/delete', methods=['GET'])
def delete_file():
    if request.method == 'GET':
        filename = request.args.get('filename')
        timestamp = request.args.get('timestamp')
        logger.debug('delete file : %s, timestamp is %s' % (filename, timestamp))
        try:
            fullfile = os.path.join(app.config['UPLOAD_PATH'], filename)

            if os.path.exists(fullfile):
                os.remove(fullfile)
                logger.debug("%s removed successfully" % fullfile)
                return jsonify({'code': 0, 'msg': ''})
            else:
                return jsonify({'code': -1, 'msg': 'File not exist'})

        except Exception as e:
            logger.debug("delete file error %s" % e)
            return jsonify({'code': -1, 'msg': 'File deleted error'})

    else:
        return jsonify({'code': -1, 'msg': 'Method not allowed'})


app.secret_key = 'aHR0cDovL3d3dy53YW5kYS5jbi8='

if __name__ == '__main__':
    #print(type(flask_db.get_user('admin')))
    #print(flask_db.get_user('admin'))
    #app.run(host='0.0.0.0', port=9000, debug=True)
    server = pywsgi.WSGIServer(('0.0.0.0', 9000), app)
    server.serve_forever()
    