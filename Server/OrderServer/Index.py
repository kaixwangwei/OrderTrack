# -*- coding: utf-8 -*-

import time
import json
from flask import Response, Blueprint, render_template, jsonify, send_from_directory
from flask import Flask, redirect, url_for, request, make_response, abort
import flask_login

import OrderTrackLogger
from models.User import User
import OrderTrackLogger
import OrderTrackDB
from KuaiDiCX.ShipperMap import SHIPPER_MAP, getLogisticalState
from SyncLogistics import *

logger = OrderTrackLogger.get_logger(__name__)
index = Blueprint('index', __name__, template_folder='index')

@index.route('/', methods=['GET', 'POST'])
@index.route('/index', methods=['GET', 'POST'])
@flask_login.login_required
def indexFunc():
    logger.debug("index page, method is %s " % request.method)
    print("[indexFunc]user fullname :%s"%flask_login.current_user.fullname)
    return render_template('index.html', shipperMap = SHIPPER_MAP)
