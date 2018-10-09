# -*- coding: utf-8 -*-
import time
import json
from flask import *
from flask_sqlalchemy import SQLAlchemy
import flask_login

import OrderTrackLogger
import OrderTrackDB
from models.LogisticsInfo import LogisticsInfo
import SyncLogistics
import LogisticsHelp

# https://spacewander.github.io/explore-flask-zh/7-blueprints.html
# http://flask.pocoo.org/docs/0.12/blueprints/
client = Blueprint('client', __name__, template_folder='client')

logger = OrderTrackLogger.get_logger(__name__)


@client.route('/client/login', methods=['GET','POST'])
def login():
    if request.method == 'POST':
        jsonpay = request.json
        username = jsonpay.get("username")
        password = jsonpay.get("password")
        logger.debug("Client login post method")
        print("username:%s ,password:%s"%(username, password))
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
        resultList = []
        jsonpay = request.json
        for jsonData in jsonpay:
            print jsonData
            dictT = LogisticsHelp.addLogistics(jsonData, jsonData['owner'])
            resultList.append(dictT)
            
        print(json.dumps(resultList))
        return json.dumps(resultList)
    else:
        return("insertrecord GET Fail")
    
@client.route("/client/getdata", methods=['GET','POST'])
#@flask_login.login_required
def getdata():
    print("[client/getdata]getdata")
    #同步一次物流信息
    SyncLogistics.startQueryAllLogistical()
    resultDict = LogisticsHelp.getAllExistJsonData()

    if request.method == 'GET':
        print json.dumps(resultDict)
        return json.dumps(resultDict)
    else:
        return json.dumps(resultDict)
    
    
@client.route('/client/delete', methods=['GET', 'POST'])
def delete():
    if request.method == 'POST':
        item = request.json
        print("operation delete POST")
        logisticsCode = item['logisticsCode']
        ret = OrderTrackDB.delRecord(logisticsCode)
        return json.dumps(ret)