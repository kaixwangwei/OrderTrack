# -*- coding: utf-8 -*-

from Models import Base
from sqlalchemy import *
from sqlalchemy.ext.declarative import declarative_base
import mysql.connector

# http://docs.sqlalchemy.org/en/latest/orm/mapping_columns.html
class RecodeList(Base):
    __tablename__ = 'record_list'

    id = Column('id', Integer, primary_key=True)
    expressCode = Column('express_code', String(128), nullable=False, unique=True)
    receiver = Column('receiver', String(128), nullable=False)
    expressDate = Column('express_date', String(128), nullable=False)
    creater = Column('creater', String(128), nullable=False)
    expressMoney = Column('express_money', Float)
    expressStatus = Column('express_status', String(1024))
    create_time = Column('create_time', String(128))
    update_time = Column('update_time', String(128))

    def __init__(self, id, expressCode, receiver, expressDate, creater, expressMoney, expressStatus, create_time, update_time):
        self.id = id
        self.expressCode = expressCode
        self.receiver = receiver
        self.expressDate = expressDate
        self.creater = creater
        self.expressMoney = expressMoney
        self.expressStatus = expressStatus
        self.create_time = create_time
        self.update_time = update_time
        
    def __repr__(self):
        return '<id is %s, username is %s, expressDate is %s, receiver is %s, create time is %s, update time is %s>' % (
            self.id, self.username, self.expressDate, self.receiver, self.create_time, self.update_time)

