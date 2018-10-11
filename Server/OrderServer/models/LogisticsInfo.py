# -*- coding: utf-8 -*-

from models.Base import *
from sqlalchemy import *
from sqlalchemy.ext.declarative import declarative_base
import mysql.connector

# http://docs.sqlalchemy.org/en/latest/orm/mapping_columns.html
class LogisticsInfo(db.Model):
    __tablename__ = 'logistics_list'

    id = Column( Integer, primary_key=True)
    logisticsCode = Column( String(128), nullable=False, unique=True)
    shipperCode = Column( String(128), nullable=False)
    receiver = Column( String(128), nullable=False)
    shipDate = Column( String(128), nullable=False)
    creater = Column( String(128), nullable=False)
    shippingMoney = Column( Float)
    logisticsInfo = Column( String(10240))
    latestLogisticsInfo = Column( String(1024))
    logisticsState = Column( Integer, nullable=False, default=0)
    create_time = Column( String(128))
    update_time = Column( String(128))
    logisticsUpdateTime = Column( String(128))
    deleted = Column( Integer, nullable=False)
   
    def __init__(self,logisticsCode, shipperCode, receiver, shipDate, creater, shippingMoney, logisticsInfo, create_time, update_time, logisticsUpdateTime = "", deleted=0):
        self.logisticsCode = logisticsCode
        self.shipperCode = shipperCode
        self.receiver = receiver
        self.shipDate = shipDate
        self.creater = creater
        self.shippingMoney = shippingMoney
        self.logisticsInfo = logisticsInfo
        self.create_time = create_time
        self.update_time = update_time
        self.logisticsUpdateTime = logisticsUpdateTime
        self.deleted = deleted
        self.logisticsState = 0

    def update(self, receiver, shipDate, creater, shippingMoney, logisticsInfo, update_time):
        self.receiver = receiver
        self.shipDate = shipDate
        self.creater = creater
        self.shippingMoney = shippingMoney
        self.logisticsInfo = logisticsInfo
        self.create_time = create_time
        self.update_time = update_time
        
    def update(self, deleted):
        self.deleted = deleted
        
    def updateFrom(self, logisticalInfo):
        self.logisticsCode = logisticalInfo.logisticsCode
        self.shipperCode = logisticalInfo.shipperCode
        self.receiver = logisticalInfo.receiver
        self.shipDate = logisticalInfo.shipDate
        self.creater = logisticalInfo.creater
        self.shippingMoney = logisticalInfo.shippingMoney
        self.logisticsInfo = logisticalInfo.logisticsInfo
        self.latestLogisticsInfo = logisticalInfo.latestLogisticsInfo
        self.logisticsState = logisticalInfo.logisticsState
        
        self.create_time = logisticalInfo.create_time
        self.update_time = logisticalInfo.update_time
        self.logisticsUpdateTime = logisticalInfo.logisticsUpdateTime
        
        self.deleted = logisticalInfo.deleted
    
    def updateFromDict(self, dictT):
        for key in dictT.keys():
            value = dictT[key]
            if key == "receiver":
                self.receiver = value
            elif key == "shipDate":
                self.shipDate = value
            elif key == "shipperCode":
                self.shipperCode = value
            elif key == "shippingMoney":
                self.shippingMoney = value

    def setLogisticsInfo(self, logisticsInfo):
        self.logisticsInfo = logisticsInfo
        
    def setLatestLogisticsInfo(self, latestLogisticsInfo):
        self.latestLogisticsInfo = latestLogisticsInfo

    def setLogisticsUpdateTime(self, logisticsUpdateTime):
        self.logisticsUpdateTime = logisticsUpdateTime
        
    def setLogisticsState(self, logisticsState):
        self.logisticsState = logisticsState
        
        
    def __repr__(self):
        return u'<id is %s, logisticsCode is %s, shipperCode is %s, shipDate is %s, receiver is %s, create time is %s, update time is %s ,deleted is %s>' % (
            self.id, self.logisticsCode, self.shipperCode, self.shipDate, self.receiver, self.create_time, self.update_time, self.deleted)

