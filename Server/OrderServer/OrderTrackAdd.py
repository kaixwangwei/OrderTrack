# -*- coding: utf-8 -*-
from flask import Blueprint, render_template, jsonify
from flask import Flask, redirect, url_for, request, render_template, make_response, abort, jsonify, \
    send_from_directory
import OrderTrackLogger
import time
import flask_login

# https://spacewander.github.io/explore-flask-zh/7-blueprints.html
# http://flask.pocoo.org/docs/0.12/blueprints/
add = Blueprint('addnew', __name__, template_folder='add')

logger = OrderTrackLogger.get_logger(__name__)



@add.route('/addnew/<int:num>')
def get(num):
    logger.debug('get method is %s' % num)
    return jsonify({'value': num + 1, 'timestamp': time.time()})

@flask_login.login_required
@add.route('/addnew', methods=['GET', 'POST'])
def addnew():
    if request.method == 'GET':
        return render_template('add/addnew.html',name=flask_login.current_user.id)
    elif request.method == 'POST':
        print request.json
        dataDict = request.form.to_dict()
        print dataDict
        print("addnew POST")
        return ""