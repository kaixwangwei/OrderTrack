# -*- coding: utf-8 -*-
from flask import *
import OrderTrackLogger
import time

# https://spacewander.github.io/explore-flask-zh/7-blueprints.html
# http://flask.pocoo.org/docs/0.12/blueprints/
client = Blueprint('client', __name__, template_folder='client')

logger = OrderTrackLogger.get_logger(__name__)


@client.route('/client/login', methods=['GET','POST'])
def login():
    if request.method == 'POST':
        jsonpay = request.json
    
        expressDB = ExpressDBHelp()
        username = jsonpay.get("username")
        password = jsonpay.get("password")
        logger.debug("Client login post method")

        user = OrderTrackDB.get_user_session(username)
        if user != None:
            logger.debug('db user id is %s, detail is %s' % (user.username, user))

            next_url = request.args.get("next")
            logger.debug('next is %s' % next_url)

            if password == user.password and username == user.username:
                # set login user
                print("admin login success")
                return "success"
        return "fail"
    else:
        return "fail"
    
    
@client.route("/client/insertrecord", methods=['GET','POST'])
def insertrecord():
    if request.method == 'POST':
        print("insertrecord")
        jsonpay = request.json
        print(jsonpay)
        return "success"
    else:
        return("insertrecord GET Fail")