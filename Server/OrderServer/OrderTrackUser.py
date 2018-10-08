# -*- coding: utf-8 -*-
from OrderTrackBase import *
from sqlalchemy import *
from sqlalchemy.ext.declarative import declarative_base
import mysql.connector

#http://docs.sqlalchemy.org/en/latest/orm/mapping_columns.html
class User(Base):
    __tablename__ = 'users'

    id = Column('id', Integer, primary_key=True)
    username = Column('username', String(128), nullable=False, unique=True)
    password = Column('password', String(128), nullable=False)
    fullname = Column('fullname', String(128))
    email = Column('email', String(128))
    phone = Column('phone', String(128))
    role = Column('role', Integer, nullable=False, default=0)  # 0 > noamal , 1 -> admin
    group_name = Column('group_name', String(128))
    create_time = Column('create_time', String(128))
    update_time = Column('update_time', String(128))

    def __init__(self, username,  password):
        self.username = username
        self.password = password

    def __init__(self, username, password, role):
        self.username = username
        self.password = password
        self.role = role
    
    def __init__(self, username, fullname, password, phone, email, role, group_name, create_time, update_time):
        self.username = username
        self.password = password
        self.fullname = fullname
        self.role = role
        self.phone = phone
        self.group_name = group_name
        self.email = email
        self.create_time = create_time
        self.update_time = update_time
    
    def updateFromDict(self, userDict):
        for key in userDict.keys():
            value = userDict[key]
            if key == "password":
                self.password = value
            elif key == "fullname":
                self.fullname = value
            elif key == "role":
                self.role = value
            elif key == "group_name":
                self.group_name = value
            elif key == "email":
                self.email = value
            elif key == "phone":
                self.phone = value                
    
    def __repr__(self):
        return '<id is %s, username is %s, password is %s, group_name:%s, email is %s, create time is %s, update time is %s>' % (
            self.id, self.username, self.password, self.group_name, self.email, self.create_time, self.update_time)

