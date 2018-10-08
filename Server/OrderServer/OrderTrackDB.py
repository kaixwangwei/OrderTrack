# -*- coding: utf-8 -*-

import time
import sys
import mysql.connector
from sqlalchemy import *
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import OrderTrackLogger

from models.Base import *
from models.User import User
from models.LogisticalInfo import LogisticalInfo


reload(sys)
sys.setdefaultencoding("utf8")


logger = OrderTrackLogger.get_logger(__name__)
user_sql = 'select * from testFlask.users where user_id = %s limit 1'


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
def get_connection_session(url=user_orm_url):
    try:
        session = sessionmaker()
        session.configure(bind=engine)
        #Base.metadata.create_all(engine)
        s = session()
        return s
    except Exception as e:
        logger.debug("get_connection_session Exception is %s" % e)
        return None


# get connection using url
def get_connection_with_url(url=user_orm_url):
    try:
        conn = engine.connect()
        return conn
    except Exception as e:
        logger.debug("Exception is %s" % e)
        return None

def creatDefaultUser():
    try:
        session = sessionmaker()
        session.configure(bind=engine)
        Base.metadata.create_all(engine)
        s = session()

        #DBSession = sessionmaker(bind=engine)
        #session = DBSession()
        # 创建新User对象:
        # 添加到session:
        use = s.query(User).filter_by(username='admin').first()
        if use:
            new_user1 = use
            new_user1.username = 'admin'
            new_user1.password = 'admin123'
            new_user1.role = '1'
        else:
            new_user1 = User(username='admin', password='admin123', role= 1)

        s.add(new_user1)
        s.commit()        

        use = s.query(User).filter_by(username='123456').first()
        if use:
            new_user2 = use
            new_user2.username = 'lxl'
            new_user2.password = '123456'
        else:
            new_user2 = User(username='123456', password='123456', role= 1)
        ret = s.add(new_user2)
        s.commit()
        return ret
    except Exception as e:
        logger.debug("creatDefaultUser Exception is %s" % e)
        return None
    
    
#添加记录到数据库，返回 true , 或者 false 
def addRecord(record):
    dictT = {}
    tmpRecord = db_session.query(LogisticalInfo).filter_by(logisticCode = record.logisticCode).first()
    dictT['logisticCode'] = record.logisticCode
    print record
    if tmpRecord and tmpRecord.deleted == 0:
        dictT['result'] = 'fail'
        dictT['reason'] = u'已经存在该快递编号'
        dictT['code'] = -1
    else :
        if tmpRecord:
            tmpRecord.updateFrom(record)
            tmpRecord.deleted = 0;
            db_session.add(tmpRecord)
        else:
            db_session.add(record)
        id = db_session.commit()
        dictT['result'] = 'success'
        dictT['reason'] = ''
        dictT['id'] = id
        dictT['code'] = 1
        dictT['url'] = '/index'
        
    return dictT

def delRecord(logistic_Code):
    print ("delRecord")
    dictT = {}
    tmpRecord = db_session.query(LogisticalInfo).filter_by(logisticCode = logistic_Code).first()
    if tmpRecord:
        tmpRecord.deleted = 1
        db_session.add(tmpRecord)
        id = db_session.commit()
        dictT['code'] = 1
    else:
        dictT['code'] = -1
        dictT['result'] = 'fail'
        dictT['reason'] = u'不存在该快递编号!'

    print tmpRecord
    
    return dictT


def updateRecord(recordDict):
    print ("updateRecord")
    dictT = {}
    tmpRecord = db_session.query(LogisticalInfo).filter_by(logisticCode = recordDict.logisticCode).first()
    if tmpRecord:
        tmpRecord.updateFrom(recordDict)
        db_session.add(tmpRecord)
        id = db_session.commit()
        dictT['code'] = 1
    else:
        dictT['code'] = -1
        dictT['result'] = 'fail'
        dictT['reason'] = u'不存在该快递编号!'

    print tmpRecord
    
    return dictT

def modifyRecord(tDict):
    print ("modifyRecord")
    dictT = {}
    tmpRecord = None
    if tDict.has_key("logisticCode") == True:
        tmpRecord = db_session.query(LogisticalInfo).filter_by(logisticCode = tDict["logisticCode"]).first()
    if tmpRecord:
        tmpRecord.updateFromDict(tDict)
        db_session.add(tmpRecord)
        id = db_session.commit()
        dictT['code'] = 1
    else:
        dictT['code'] = -1
        dictT['result'] = 'fail'
        dictT['reason'] = u'不存在该用户!'

    print tmpRecord
    
    return dictT


#读取没有被标记为删除的项目
def getAllExistData():
    record = db_session.query(LogisticalInfo).filter(LogisticalInfo.deleted == 0).filter_by().all()
    return record

#读取快递信息未完成的项目
def getAllNeedSyncLogistical():
    record = db_session.query(LogisticalInfo).filter(LogisticalInfo.logisticsState != 3).filter(LogisticalInfo.deleted == 0).filter_by().all()
    return record




###########################用户相关
def getAllUsers():
    users = db_session.query(User).filter_by().all()
    return users


def modifyUser(tDict):
    print ("modifyUser")
    dictT = {}
    tmpUser = None
    if tDict.has_key("username") == True:
        tmpUser = db_session.query(User).filter_by(username = tDict["username"]).first()
    if tmpUser:
        tmpUser.updateFromDict(tDict)
        db_session.add(tmpUser)
        id = db_session.commit()
        dictT['code'] = 1
    else:
        dictT['code'] = -1
        dictT['result'] = 'fail'
        dictT['reason'] = u'不存在该快递编号!'

    print tmpUser
    
    return dictT

def addUser(user):
    dictT = {}
    tmpUser = db_session.query(User).filter_by(username = user.username).first()
    dictT['username'] = user.username
    print user
    if tmpUser:
        dictT['result'] = 'fail'
        dictT['reason'] = u'已经存在该用户！'
        dictT['code'] = -1
    else :
        db_session.add(user)
        id = db_session.commit()
        dictT['result'] = 'success'
        dictT['reason'] = ''
        dictT['id'] = id
        dictT['code'] = 1
        dictT['url'] = '/admin/users'
        
    return dictT


def delUser(username):
    print ("DB delUser")
    dictT = {}
    tmpUser = db_session.query(User).filter_by(username = username).first()
    if tmpUser:
        db_session.delete(tmpUser)
        id = db_session.commit()
        dictT['code'] = 1
    else:
        dictT['code'] = -1
        dictT['result'] = 'fail'
        dictT['reason'] = u'不存在该用户!'

    return dictT


if __name__ == '__main__':
    creatDefaultUser()
    #print(get_user_session('admin'))
    

    #print(get_user('admin'))
