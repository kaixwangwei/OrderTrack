# -*- coding: utf-8 -*-
import mysql.connector
from sqlalchemy import *
from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from flask_sqlalchemy import SQLAlchemy
from OrderConfig import *

#创建mysql用户 , 并创建库， 并赋予相应权限
#create user 'OrderTrack'@'%' identified by 'wangweiLxl';
#create database OrderTrackDB default character set utf8 collate utf8_general_ci;
#grant all privileges on `OrderTrackDB`.* to 'OrderTrack'@'%' identified by 'wangweiLxl';

#修改MYSQL密码：
#use mysql;
#update user set authentication_string=password('wangweiLxl') where user='root';
#flush privileges;

user_orm_url = SQLALCHEMY_DATABASE_URI

db = SQLAlchemy()

engine = create_engine(user_orm_url, convert_unicode=True, pool_size=100, max_overflow=100)
db_session = scoped_session(sessionmaker(autocommit=False,
                                         autoflush=False,
                                         bind=engine))
Base = declarative_base()
Base.query = db_session.query_property()


