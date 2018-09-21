# -*- coding: utf-8 -*-
import mysql.connector
from sqlalchemy import *
from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base



user_orm_url = 'mysql+mysqlconnector://root:123456@127.0.0.1:3306/testFlask'

config = {
    'host': '127.0.0.1',
    'user': 'root',
    'password': '123456',
    'port': 3306,
    'database': 'testFlask',
    'charset': 'utf8'
}

engine = create_engine(user_orm_url, convert_unicode=True)
db_session = scoped_session(sessionmaker(autocommit=False,
                                         autoflush=False,
                                         bind=engine))
Base = declarative_base()
Base.query = db_session.query_property()
