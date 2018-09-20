# -*- coding: utf-8 -*-
import mysql.connector
from Models import Base
from sqlalchemy import *
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import OrderTrackLogger
from OrderTrackUser import User

config = {
    'host': '127.0.0.1',
    'user': 'root',
    'password': '123456',
    'port': 3306,
    'database': 'testFlask',
    'charset': 'utf8'
}
user_orm_url = 'mysql+mysqlconnector://root:123456@127.0.0.1:3306/testFlask'

user_sql = 'select * from testFlask.users where user_id = %s limit 1'
logger = OrderTrackLogger.get_logger(__name__)




# get database connection using mysql.connector
def get_connection():
    try:
        conn = mysql.connector.connect(**config)
        return conn
    except Exception as e:
        logger.debug("Exception is %s" % e)
        return None

        # get database connection


# get user by user_id
def get_user(user_id):
    try:
        conn = get_connection()
        if conn:
            cursor = conn.cursor()
            cursor.execute(user_sql, (user_id,))
            result = cursor.fetchall()
            logger.debug("conn is %s" % conn)
            return result
        else:
            logger.debug("conn is %s" % conn)
            return None
        logger.debug("conn is %s" % conn)
        return None
    except Exception as e:
        logger.debug("Exception is %s" % e)
        return None
    finally:
        cursor.close()
        conn.close()





# for login
# SQLAlchemy orm
def get_user_session(user_id):
    try:
        print("get_user_session user_id:%s"%(user_id))
        engine = create_engine(user_orm_url, echo=True)
        session = sessionmaker()
        session.configure(bind=engine)
        Base.metadata.create_all(engine)
        s = session()

        ret = s.query(User).filter_by(username=user_id).first()
        return ret
    except Exception as e:
        logger.debug("get_user_session Exception is %s" % e)
        return None


# get connection session
def get_connection_session(url):
    try:
        engine = create_engine(url, echo=True)
        session = sessionmaker()
        session.configure(bind=engine)
        Base.metadata.create_all(engine)
        s = session()
        return s
    except Exception as e:
        logger.debug("get_connection_session Exception is %s" % e)
        return None


# get connection using url
def get_connection_with_url(url):
    try:
        engine = create_engine(url, echo=True)
        conn = engine.connect()
        return conn
    except Exception as e:
        logger.debug("Exception is %s" % e)
        return None


if __name__ == '__main__':
    print(get_user_session('admin'))
    

    #print(get_user('admin'))
