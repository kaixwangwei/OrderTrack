# -*- coding: utf-8 -*-
from flask import *
from flask_sqlalchemy import SQLAlchemy
import OrderTrackLogger
import OrderTrackDB
from OrderTrackRecord import RecodeList
import time
from OrderTrackBase import *
import json
import flask_login

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
        for item in jsonpay:
            dictT = {}
            #expressDate': u'2018-09-18', u'expressMoney': 0, u'expressCode': u'tgg', 
            #u'syncToServer': 0, u'owner': u'123456', u'receiver': u'tffh
            expressCode = item['expressCode']
            expressDate = item['expressDate']
            creater = item['owner']
            expressMoney = item['expressMoney']
            receiver = item['receiver']
            expressStatus = ""
            
            localTime = time.localtime() 
            strTime = time.strftime("%Y-%m-%d %H:%M:%S", localTime)             

            create_time = strTime
            update_time = strTime
            
            record = db_session.query(RecodeList).filter_by(expressCode=expressCode).first()
            dictT['expressCode'] = expressCode
            print record
            if record:
                dictT['result'] = 'fail'
                dictT['reason'] = 'already exit'
            else :
                record = RecodeList(expressCode, receiver, expressDate, creater, expressMoney, expressStatus, create_time, update_time)
                db_session.add(record)
                id = db_session.commit()
                print id
                dictT['result'] = 'success'
                dictT['reason'] = ''
                
            resultList.append(dictT)
            print item
        
        print(json.dumps(resultList))
        return json.dumps(resultList)
    else:
        return("insertrecord GET Fail")
    
@client.route("/client/getdata", methods=['GET','POST'])
#@flask_login.login_required
def getdata():
    print("getdata")
    resultDict = {}
    dataList = []
    record = db_session.query(RecodeList).all()
    
    for item in record:
        dictT = {}
        dictT['express_code'] = item.expressCode
        dictT['receiver'] = item.receiver
        dictT['express_date'] = item.expressDate
        dictT['sender'] = item.creater
        dictT['express_money'] = item.expressMoney
        dictT['current_status'] = item.expressStatus
        dictT['joinTime'] = item.create_time
        dictT['updateTime'] = item.update_time
        dataList.append(dictT)
    resultDict['code'] = 0
    resultDict['msg'] = ""
    resultDict['count'] = len(dataList)
    resultDict['data'] = dataList
    if request.method == 'GET':
        print json.dumps(resultDict)
        return json.dumps(resultDict)
    else:
        return json.dumps(resultDict)