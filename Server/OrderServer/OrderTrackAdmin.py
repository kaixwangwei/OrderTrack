# -*- coding: utf-8 -*-
from flask import Blueprint, render_template, jsonify
from flask import Flask, redirect, url_for, request, render_template, make_response, abort, jsonify, \
    send_from_directory
import OrderTrackLogger
import OrderTrackDB
import time
import flask_login
from OrderTrackRecord import RecodeList
import time
import json

# https://spacewander.github.io/explore-flask-zh/7-blueprints.html
# http://flask.pocoo.org/docs/0.12/blueprints/
admin = Blueprint('admin', __name__, template_folder='admin')

logger = OrderTrackLogger.get_logger(__name__)

@flask_login.login_required
@admin.route("/admin/user", methods=['GET','POST'])
def user():
    if request.method == 'GET':
        logger.debug('user GET')
        return render_template('/admin/userindex.html',name=flask_login.current_user)
    else :
        return "POST NO RESPONSE"




@admin.route('/admin/<page>')
def admin_url(page):
    logger.debug('admin page is %s' % page)
    return render_template('/admin/%s.html' % page)


@admin.route('/admin/<int:num>')
def get(num):
    logger.debug('get method is %s' % num)
    return jsonify({'value': num + 1, 'timestamp': time.time()})


@admin.route('/admin/getdata')
def getdata():
    return ""