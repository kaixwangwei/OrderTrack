# -*- coding: utf-8 -*-
from flask import Blueprint, render_template, jsonify
from flask import Flask, redirect, url_for, request, render_template, make_response, abort, jsonify, \
    send_from_directory
import OrderTrackLogger
import OrderTrackDB
import time
import flask_login
from models.LogisticalInfo import LogisticalInfo
import time
import json
from KuaiDiCX.ShipperMap import SHIPPER_MAP, getLogisticalState


# https://spacewander.github.io/explore-flask-zh/7-blueprints.html
# http://flask.pocoo.org/docs/0.12/blueprints/
operation = Blueprint('addnew', __name__, template_folder='operation')

logger = OrderTrackLogger.get_logger(__name__)



def creatLogisticalInfo(logisticsInfo):
    retStr = u""
    try:
        logisticsDict = json.loads(logisticsInfo)
        for item in reversed(logisticsDict):
            #print item['AcceptStation']
            retStr = retStr  + "[" + item['AcceptTime'] + "]" + item['AcceptStation'] + "</br>"
    except :
        retStr = ""
    return retStr


@operation.route("/getdata", methods=['GET','POST'])
#@flask_login.login_required
def getdata():
    print("operation getdata")
    resultDict = {}
    dataList = []
    record = OrderTrackDB.getAllExistData()
    
    for item in record:
        dictT = {}
        dictT['logisticCode'] = item.logisticCode
        dictT['shipperCode'] = SHIPPER_MAP[item.shipperCode]
        dictT['receiver'] = item.receiver
        dictT['shipDate'] = item.shipDate
        dictT['sender'] = item.creater
        dictT['shippingMoney'] = item.shippingMoney
        dictT['logisticsState'] = getLogisticalState(item.logisticsState)
        dictT['logisticsInfo'] = creatLogisticalInfo(item.logisticsInfo)
        dictT['joinTime'] = item.create_time
        dictT['updateTime'] = item.update_time
        dataList.append(dictT)
    resultDict['code'] = 0
    resultDict['msg'] = ""
    resultDict['count'] = len(dataList)
    resultDict['data'] = dataList
    if request.method == 'GET':
        #print json.dumps(resultDict)
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
        
        #{u'date': u'2018-09-24', u'money': u'123', u'kuaidigongshi': u'\u987a\u4e30', u'logisticCode': u'2131', u'receiver': u'admin'}
        logisticCode = item['logisticCode']
        shipperCode = item['shipperCode']
        shipDate = item['date']
        creater = flask_login.current_user.id
        shippingMoney = item['money']
        receiver = item['receiver']
        logisticsInfo = ""

        localTime = time.localtime() 
        strTime = time.strftime("%Y-%m-%d %H:%M:%S", localTime)             

        create_time = strTime
        update_time = strTime

        record = LogisticalInfo(logisticCode, shipperCode, receiver, shipDate, creater, shippingMoney, logisticsInfo, create_time, update_time)
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
        print("operation delete POST")
        logisticCode = item['logisticCode']
        ret = OrderTrackDB.delRecord(logisticCode)
        return json.dumps(ret)
    
@flask_login.login_required
@operation.route('/modify', methods=['GET', 'POST'])
def modify():
    if request.method == 'GET':
        return render_template('test.html',name=flask_login.current_user.id)
    elif request.method == 'POST':
        print("operation modify POST")        
        item = request.json
        ret = OrderTrackDB.modifyRecord(item)
        return json.dumps(ret)