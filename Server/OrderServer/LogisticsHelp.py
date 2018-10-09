# -*- coding: utf-8 -*-

import time
import sys
import json
import mysql.connector
from sqlalchemy import *
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import OrderTrackLogger

from models.Base import *
from models.User import User
from KuaiDiCX.ShipperMap import SHIPPER_MAP, getLogisticalState
from models.LogisticsInfo import LogisticsInfo
import SyncLogistics
import OrderTrackDB

reload(sys)
sys.setdefaultencoding("utf8")


def creatLogisticalInfo(logisticsInfo, logisticsUpdateTime):
    retStr = u""

    try:
        logisticsDict = json.loads(logisticsInfo)
        for item in reversed(logisticsDict):
            retStr = retStr  + "[" + item['AcceptTime'] + "]" + item['AcceptStation'] + "</br>"
    except Exception,e:
        print e
        print("creatLogisticalInfo Exception")
        retStr = ""
        
    if logisticsUpdateTime != None and logisticsUpdateTime != "":
        retStr = retStr + "[物流信息最新同步时间][" + logisticsUpdateTime + "]"

    return retStr

def getFullName(username):
    tmpUser = OrderTrackDB.get_user_session(username)
    if tmpUser == None:
        return None
    
    if tmpUser.fullname != None:
        return tmpUser.fullname
    return username
    

#读取没有被标记为删除的项目,按照json的方式输出
def getAllExistJsonData():
    resultDict = {}
    dataList = []
    record = OrderTrackDB.getAllExistData()
    for item in record:
        dictT = {}
        dictT['id'] = item.id
        dictT['logisticsCode'] = item.logisticsCode
        dictT['shipperCodeCH'] = SHIPPER_MAP[item.shipperCode]
        dictT['shipperCode'] = item.shipperCode
        dictT['receiver'] = item.receiver
        dictT['shipDate'] = item.shipDate
        dictT['sender'] = getFullName(item.creater)
        dictT['shippingMoney'] = item.shippingMoney
        dictT['logisticsState'] = getLogisticalState(item.logisticsState)
        dictT['logisticsInfo'] = creatLogisticalInfo(item.logisticsInfo, item.logisticsUpdateTime)
        dictT['latestLogisticsInfo'] = item.latestLogisticsInfo
        
        dictT['joinTime'] = item.create_time
        dictT['updateTime'] = item.update_time
        dataList.append(dictT)
    resultDict['code'] = 0
    resultDict['msg'] = ""
    resultDict['count'] = len(dataList)
    resultDict['data'] = dataList    
    return resultDict


def addLogistics(jsonData, creater):
    localTime = time.localtime()
    strTime = time.strftime("%Y-%m-%d %H:%M:%S", localTime)
    
    logisticsCode = jsonData['logisticsCode']
    shipperCode = jsonData['shipperCode']
    shipDate = jsonData['shipDate']
    creater = creater
    shippingMoney = jsonData['shippingMoney']
    receiver = jsonData['receiver']
    logisticsInfo = ""

    localTime = time.localtime()
    strTime = time.strftime("%Y-%m-%d %H:%M:%S", localTime)

    create_time = strTime
    update_time = strTime

    record = LogisticsInfo(logisticsCode, shipperCode, receiver, shipDate, creater, shippingMoney, logisticsInfo, create_time, update_time)
    ret = OrderTrackDB.addRecord(record)
    if ret["code"] == 1:
        SyncLogistics.startQueryLogistical(logisticsCode)
    return json.dumps(ret)
    