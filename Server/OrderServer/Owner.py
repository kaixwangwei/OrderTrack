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
owner = Blueprint('owner', __name__, template_folder='owner')

logger = OrderTrackLogger.get_logger(__name__)



@flask_login.login_required
@owner.route("/owner/info", methods=['GET','POST'])
def user():
    if request.method == 'GET':
        logger.debug('user GET')
        return render_template('/owner/owner.html')


