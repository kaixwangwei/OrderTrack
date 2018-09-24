# -*- coding: utf-8 -*-
from OrderTrackBase import *
from sqlalchemy import *
from sqlalchemy.ext.declarative import declarative_base
import mysql.connector

# http://docs.sqlalchemy.org/en/latest/orm/mapping_columns.html
class User(Base):
    __tablename__ = 'users'

    id = Column('id', Integer, primary_key=True)
    username = Column('user_id', String(128), nullable=False, unique=True)
    email = Column('email', String(128))
    password = Column('password', String(128), nullable=False)
    group_name = Column('group_name', String(128))
    create_time = Column('create_time', String(128))
    update_time = Column('update_time', String(128))

    def __init__(self, id, username, email, password, group_name, create_time, update_time):
        self.id = id
        self.username = username
        self.email = email
        self.password = password
        self.group_name = group_name
        self.create_time = create_time
        self.update_time = update_time

    def __init__(self, username,  password):
        self.username = username
        self.password = password

    def __repr__(self):
        return '<id is %s, username is %s, password is %s, group_name:%s, email is %s, create time is %s, update time is %s>' % (
            self.id, self.username, self.password, self.group_name, self.email, self.create_time, self.update_time)

