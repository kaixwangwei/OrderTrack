# -*- coding: utf-8 -*-
from flask import Blueprint, render_template, jsonify
from flask import Flask, redirect, url_for, request, render_template, make_response, abort, jsonify, \
    send_from_directory
import OrderTrackLogger
import OrderTrackDB
import time
import flask_login
from models.User import User
import time
import json

# https://spacewander.github.io/explore-flask-zh/7-blueprints.html
# http://flask.pocoo.org/docs/0.12/blueprints/
admin = Blueprint('admin', __name__, template_folder='admin')

logger = OrderTrackLogger.get_logger(__name__)



@flask_login.login_required
@admin.route("/admin/users", methods=['GET','POST'])
def user():
    if request.method == 'GET':
        logger.debug('user GET')
        return render_template('/admin/usersindex.html')





@flask_login.login_required
@admin.route('/admin/<page>')
def admin_url(page):
    logger.debug('admin page is %s' % page)
    return render_template('/admin/%s.html' % page)



@flask_login.login_required
@admin.route('/admin/<int:num>')
def get(num):
    logger.debug('get method is %s' % num)
    return jsonify({'value': num + 1, 'timestamp': time.time()})



@flask_login.login_required
@admin.route('/admin/adduser', methods=['GET','POST'])
def adduser():
    if request.method == 'GET':
        logger.debug('adduser GET')
        return render_template('/admin/adduser.html')
    elif request.method == 'POST':
        print("adduser POST")
        item = request.json
        print item
        localTime = time.localtime()
        strTime = time.strftime("%Y-%m-%d %H:%M:%S", localTime)
    
        #{u'username': u'123', u're_password': u'123456', u'group_name': u'\u5218\u5bb6\u5927\u9662', u'phone': u'15882387221', u'fullname': u'\u5218\u5c0f\u6797', u'password': u'123456', u'email': u'123456@qq.com'}
        username = item['username']
        password = item['password']
        fullname = item['fullname']
        group_name = item['group_name']
        phone = item['phone']
        email = item['email']
        role = item['role']

        localTime = time.localtime() 
        strTime = time.strftime("%Y-%m-%d %H:%M:%S", localTime)             
    
        create_time = strTime
        update_time = strTime
    
        user = User(username, fullname, password, phone, email, role, group_name, create_time, update_time)
        ret = OrderTrackDB.addUser(user)
        print ret
        return json.dumps(ret)


@flask_login.login_required
@admin.route('/admin/userslist', methods=['GET','POST'])
def userslist():
    print("userslist")
    resultDict = {}
    dataList = []
    users = OrderTrackDB.getAllUsers()
    
    for user in users:
        dictT = {}
        dictT['username'] = user.username
        dictT['fullname'] = user.fullname
        dictT['password'] = user.password
        dictT['group_name'] = user.group_name
        dictT['role'] = user.role
        dictT['phone'] = user.phone
        dictT['email'] = user.email
        dataList.append(dictT)
    resultDict['code'] = 0
    resultDict['msg'] = ""
    resultDict['count'] = len(dataList)
    resultDict['data'] = dataList
    if request.method == 'GET':
        #print json.dumps(resultDict)
        return json.dumps(resultDict)
    else:
        return json.dumps(resultDict)
    
    
@flask_login.login_required
@admin.route('/admin/delete', methods=['GET', 'POST'])
def delete():
    if request.method == 'GET':
        return render_template('admin/users.html')
    elif request.method == 'POST':
        
        item = request.json
        
        print item
        print("admin delete POST")
        username = item['username']
        ret = OrderTrackDB.delUser(username)
        return json.dumps(ret)
    
    
@flask_login.login_required
@admin.route('/admin/modify', methods=['GET', 'POST'])
def modify():
    if request.method == 'GET':
        return render_template('test.html')
    elif request.method == 'POST':

        item = request.json

        print item
        print("user modify POST")
        ret = OrderTrackDB.modifyUser(item)
        return json.dumps(ret)