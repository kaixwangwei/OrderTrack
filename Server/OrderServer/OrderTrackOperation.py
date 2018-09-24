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
operation = Blueprint('addnew', __name__, template_folder='operation')

logger = OrderTrackLogger.get_logger(__name__)

@operation.route("/getdata", methods=['GET','POST'])
#@flask_login.login_required
def getdata():
    print("getdata")
    resultDict = {}
    dataList = []
    record = OrderTrackDB.getAllExistData()
    
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



@flask_login.login_required
@operation.route('/addnew', methods=['GET', 'POST'])
def addnew():
    if request.method == 'GET':
        return render_template('operation/addnew.html',name=flask_login.current_user.id)
    elif request.method == 'POST':
        item = request.json
        
        localTime = time.localtime()
        strTime = time.strftime("%Y-%m-%d %H:%M:%S", localTime)
        
        #{u'date': u'2018-09-24', u'money': u'123', u'kuaidigongshi': u'\u987a\u4e30', u'express_code': u'2131', u'receiver': u'admin'}
        expressCode = item['express_code']
        expressDate = item['date']
        creater = flask_login.current_user.id
        expressMoney = item['money']
        receiver = item['receiver']
        expressStatus = ""

        localTime = time.localtime() 
        strTime = time.strftime("%Y-%m-%d %H:%M:%S", localTime)             

        create_time = strTime
        update_time = strTime

        record = RecodeList(expressCode, receiver, expressDate, creater, expressMoney, expressStatus, create_time, update_time)
        ret = OrderTrackDB.addRecord(record)

        print("addnew POST")
        print ret
        return json.dumps(ret)


    
@flask_login.login_required
@operation.route('/delete', methods=['GET', 'POST'])
def delete():
    if request.method == 'GET':
        return render_template('operation/addnew.html',name=flask_login.current_user.id)
    elif request.method == 'POST':
        
        item = request.json
        
        print item
        print("delete POST")
        expressCode = item['express_code']
        ret = OrderTrackDB.delRecord(expressCode)
        return json.dumps(ret)