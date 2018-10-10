# -*- coding: utf-8 -*-

import time
import json
from flask import Blueprint, render_template, jsonify, send_from_directory
from flask import Flask, redirect, url_for, request, make_response, abort
import flask_login

from KuaiDiCX.ShipperMap import SHIPPER_MAP, getLogisticalState
from models.User import User
import OrderTrackLogger
import OrderTrackDB
import OrderTrackLogger
from User import User


logger = OrderTrackLogger.get_logger(__name__)
login = Blueprint('login', __name__, template_folder='login')

def is_safe_url(next_url):
    logger.debug("next url is %s:" % next_url)
    return True

def setUserByDict(username, tmpUser):
    user = User()
    user.id = username
    
    if tmpUser.fullname == None:
        user.fullname = tmpUser.username
    else:
        user.fullname = tmpUser.fullname
    user.role = tmpUser.role
    print("user.fullname = %s, user.role = %s"%(user.fullname, user.role))
    return user


@login.route('/login', methods=['GET', 'POST'])
def loginFunc():
    if request.method == 'POST':
        logger.debug("login post method")
        username = request.form['username']
        password = request.form['password']

        tmpUser = OrderTrackDB.get_user_session(username)
        if tmpUser != None:
            logger.debug('db user id is %s, detail is %s' % (tmpUser.username, tmpUser))

            next_url = request.args.get("next")
            logger.debug('next is %s' % next_url)

            if password == tmpUser.password and username == tmpUser.username:
                # set login user
                print("admin login success")
                user = setUserByDict(username, tmpUser)
                flask_login.login_user(user,remember=True)
    
                resp = make_response(render_template('index.html', shipperMap = SHIPPER_MAP))
                resp.set_cookie('username', username)
                if not is_safe_url(next_url):
                    return abort(400)
                return redirect(next_url or url_for('index.indexFunc'))
            else:
                return abort(401)
        else:
            return abort(401)

    logger.debug("login get method")
    return render_template('login.html')


@login.route('/logout', methods=['GET', 'POST'])
@flask_login.login_required
def logoutFunc():
    # remove the username from the session if it's there
    logger.debug("logout page")
    flask_login.logout_user()
    return redirect(url_for('login.loginFunc'))
