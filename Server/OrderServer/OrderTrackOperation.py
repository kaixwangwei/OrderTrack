# -*- coding: utf-8 -*-

from flask import Blueprint, render_template, jsonify, send_from_directory
from flask import Flask, redirect, url_for, request, make_response, abort

import OrderTrackLogger
import OrderTrackDB
import time
import flask_login
from models.LogisticsInfo import LogisticsInfo
import json
from KuaiDiCX.ShipperMap import SHIPPER_MAP, getLogisticalState
import SyncLogistics
import LogisticsHelp


operation = Blueprint('addnew', __name__, template_folder='operation')

logger = OrderTrackLogger.get_logger(__name__)


@operation.route("/getdata", methods=['GET','POST'])
def getdata():
    print("operation getdata")

    resultDict = {}

    #同步一次物流信息
    SyncLogistics.startQueryAllLogistical()
    resultDict = LogisticsHelp.getAllExistJsonData()

    if request.method == 'GET':
        #print json.dumps(resultDict)
        return json.dumps(resultDict)
    else:
        return json.dumps(resultDict)



@operation.route('/addnew', methods=['GET', 'POST'])
def addnew():
    if request.method == 'GET':
        return render_template('operation/addnew.html',name=flask_login.current_user.id)
    elif request.method == 'POST':
        print("addnew POST")
        
        jsonData = request.json
        return LogisticsHelp.addLogistics(jsonData, flask_login.current_user.id)


    
@flask_login.login_required
@operation.route('/delete', methods=['GET', 'POST'])
def delete():
    if request.method == 'GET':
        return render_template('operation/addnew.html',name=flask_login.current_user.id)
    elif request.method == 'POST':
        item = request.json
        print("operation delete POST")
        logisticsCode = item['logisticsCode']
        ret = OrderTrackDB.delRecord(logisticsCode)
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
        if ret["code"] == 1 and item.has_key("logisticsCode") == True:
            SyncLogistics.startQueryLogistical(item['logisticsCode'])
        return json.dumps(ret)