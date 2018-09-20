# -*- coding: utf-8 -*-
from flask import Blueprint, render_template, jsonify
import OrderTrackLogger
import time

# https://spacewander.github.io/explore-flask-zh/7-blueprints.html
# http://flask.pocoo.org/docs/0.12/blueprints/
addnew = Blueprint('addnew', __name__, template_folder='admin')

logger = OrderTrackLogger.get_logger(__name__)


@addnew.route('/admin/<page>')
def admin_url(page):
    logger.debug('admin page is %s' % page)
    return render_template('/admin/%s.html' % page)


@addnew.route('/admin/<int:num>')
def get(num):
    logger.debug('get method is %s' % num)
    return jsonify({'value': num + 1, 'timestamp': time.time()})


@addnew.route('/addnew')
def getdata():
    return "";