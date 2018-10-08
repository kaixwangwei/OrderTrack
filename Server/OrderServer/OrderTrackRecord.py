# -*- coding: utf-8 -*-

from OrderTrackBase import Base
from sqlalchemy import *
from sqlalchemy.ext.declarative import declarative_base
import mysql.connector

# http://docs.sqlalchemy.org/en/latest/orm/mapping_columns.html
class RecodeList(Base):
    __tablename__ = 'record_list'

    id = Column( Integer, primary_key=True)
    logisticCode = Column( String(128), nullable=False, unique=True)
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
    deleted = Column( Integer, nullable=False)

    def __init__(self,logisticCode, ShipperCode, receiver, shipDate, creater, shippingMoney, logisticsInfo, create_time, update_time, deleted=0):
        self.logisticCode = logisticCode
        self.shipperCode = ShipperCode
        self.receiver = receiver
        self.shipDate = shipDate
        self.creater = creater
        self.shippingMoney = shippingMoney
        self.logisticsInfo = logisticsInfo
        self.create_time = create_time
        self.update_time = update_time
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
        
    def updateFrom(self, record):
        self.logisticCode = record.logisticCode
        self.shipperCode = record.shipperCode
        self.receiver = record.receiver
        self.shipDate = record.shipDate
        self.creater = record.creater
        self.shippingMoney = record.shippingMoney
        self.logisticsInfo = record.logisticsInfo
        self.latestLogisticsInfo = record.latestLogisticsInfo
        self.logisticsState = record.logisticsState
        
        self.create_time = record.create_time
        self.update_time = record.update_time
        self.deleted = record.deleted
    
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

    def setLogisticsState(self, logisticsState):
        self.logisticsState = logisticsState
        
        
    def __repr__(self):
        return u'<id is %s, logisticCode is %s, shipperCode is %s, shipDate is %s, receiver is %s, create time is %s, update time is %s ,deleted is %s>' % (
            self.id, self.logisticCode, self.shipperCode, self.shipDate, self.receiver, self.create_time, self.update_time, self.deleted)

