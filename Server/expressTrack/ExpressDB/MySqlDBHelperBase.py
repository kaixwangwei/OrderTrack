#!/usr/bin/python
# -*- coding: UTF-8 -*-


import re
import datetime
import collections
import pymysql.cursors
from ExpressDB.MySqlDef import *

class MySqlDBHelperBase:
    def __init__(self):
        self.mServerIp = MYSQL_ADDR
        self.mUser = MYSQL_USERNAME
        self.mPassWord = MYSQL_PASSWD
        self.mConn = None
        self.mDebug = False
        self.connect()
        self.Init()

    def connect(self):
        self.mConn = pymysql.connect(host=self.mServerIp, user=self.mUser, password=self.mPassWord,
                                     charset='utf8mb4', cursorclass=pymysql.cursors.DictCursor)

    #读取数据库列表
    def queryDataBasesList(self):
        cursor = self.mConn.cursor()
        dataBaseList = []
        try:
            sql = 'show databases'
            cursor.execute(sql)
            data = cursor.fetchall()
            for item in data:
                dataBaseList.append(item['Database'])
        finally:
            cursor.close()
        return dataBaseList

    #查询数据库中 数据表的列表
    def queryTablesList(self, dataBase):
        cursor = self.mConn.cursor()
        tableNameList = []
        try:
            sql = "select table_name from information_schema.tables where table_schema='%s'"%(dataBase)
            cursor.execute(sql)
            data = cursor.fetchall()
            for item in data:
                tableNameList.append(item['table_name'])
        finally:
            cursor.close()
        return tableNameList

    #查询指定数据库中指定数据表的 数据项
    def queryTableItem(self, DBName, tableName):
        cursor = self.mConn.cursor()
        dataItemList = []
        try:
            sql = "select COLUMN_NAME from information_schema.COLUMNS where table_name = '%s' and table_schema = '%s'"%(tableName, DBName)
            cursor.execute(sql)
            data = cursor.fetchall()
            for item in data:
                dataItemList.append(item['COLUMN_NAME'])
        finally:
            cursor.close()
        return dataItemList
    
    #创建一个新的数据库
    def creatDataBase(self, DataBaseName):
        cursor = self.mConn.cursor()
        try:
            sql = "CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARSET utf8 COLLATE utf8_general_ci"%(DataBaseName)
            cursor.execute(sql)
            self.mConn.commit()
        finally:
            cursor.close()

    #在给定的数据库中增加一个数据表
    def creatTable(self, DataBase, creatTableSql):
        changeDB = "use %s"%(DataBase)
        cursor = self.mConn.cursor()
        try:
            cursor.execute(changeDB)
            cursor.execute(creatTableSql)
            self.mConn.commit()
        finally:
            cursor.close()

    #给某一个具体 数据库中的数据表增加数据项
    def insertDataItem(self, DBName, table, dataItem, dateItemAttr):
        sql = "alter table `%s` add `%s` '%s'"%(table, dataItem, dateItemAttr)
        changeDB = "use %s"%(DBName)
        cursor = self.mConn.cursor()
        try:
            cursor.execute(changeDB)
            cursor.execute(sql)
            self.mConn.commit()
        finally:
            cursor.close()
            
    #从sql命令中，获取到数据表的名称
    def getTableNameFromCmd(self, sqlCmd):
        if self.mDebug:
            print("[getTableNameFromCmd]sqlCmd:%s"%(sqlCmd))
        ret = re.findall("`([^`].*?)`", sqlCmd)
        if ret != None and ret != []:
            return ret[0]
        else:
            return []

    #从sql命令中获取到数据项的名称
    def getDataItemFromCmd(self, sqlCmd):
        if self.mDebug:
            print("[getDataItemFromCmd]sqlCmd:%s"%(sqlCmd))
        dataItemDict = collections.OrderedDict()
        ret = re.findall("^`([^`].*?)` ([^,].*?),$", sqlCmd, re.M)
        if ret != None and ret != []:
            for item in ret:
                dataItemDict[item[0]] = item[1]
        return dataItemDict
    
    #初始化数据库，会根据 DATABASE_TABLES 中的sql命令，创建相关的数据库 和 数据表
    def Init(self):
        print("We will start Init DB")
        dataBaseList = self.queryDataBasesList()
        for database in DATABASE_TABLES.keys():
            #print database
            if database in dataBaseList:
                #如果该 databse 已经创建了， 则判断 table 是否都已经创建
                creatTableCmds = DATABASE_TABLES[database]
                tableNameList = self.queryTablesList(database)
                tableNum = len(creatTableCmds)
                for sqlCmd in creatTableCmds:
                    tableName = self.getTableNameFromCmd(sqlCmd)
                    if tableName != None:
                        if tableName in tableNameList:
                            #表已经存在了， 开始判断数据段是否存在
                            dataItemExistList = self.queryTableItem(database, tableName)
                            dataItemInCmd = self.getDataItemFromCmd(sqlCmd)
                            print("[Init]DataBase : %s, table :%s , exist data sqlCmd : %s"%(database, tableName, ",".join(dataItemExistList)))
                            for dataItem in dataItemInCmd.keys():
                                if self.mDebug == True:
                                    print ("********** item : %s - %s"%(dataItem, dataItemInCmd[dataItem]))
                                if dataItem in dataItemExistList:
                                    pass
                                else:
                                    #需要添加数据项
                                    self.insertDataItem(database, tableName, dataItem, dataItemInCmd[dataItem])
                        else:
                            self.creatTable(database, sqlCmd)
                    else:
                        print("[ERROR]tableName == None, When creat table.%s"%(sqlCmd))
            else:
                #该 DataBase 并未创建，则创建该DataBase
                self.creatDataBase(database)

    def __dictFactory(self, cursor, row):
        d = {}
        print row
        for idx, col in enumerate(cursor.description):
            print row[idx]
            print("[__dictFactory]idx=%s"%(idx))
            print row[idx]
            print col[0].upper()
            d[col[0].upper()] = row[idx]
        return d
###################################################################################
#以上的只用作初始化数据库的，不做具体增删改查的操作
###################################################################################
#CREATE DATABASE IF NOT EXISTS GERRIT DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

    
    #插入/添加 数据
    def insert(self, tableName, itemDict, DBName=ORDER_TRACK_DB):
        ret = True
        param = value = ''
        try:
            initDB = "use %s"%(DBName)
            cursor = self.mConn.cursor()            
            cursor.execute(initDB)
            for key in itemDict.keys():
                param = param + key + ","
                value = "%s'%s',"%(value, itemDict[key])
            param=param[:-1]
            value=value[:-1]
            sql="insert into %s (%s) values(%s)"%(tableName, param, value)
            cursor.execute(sql)
            self.mConn.commit()
        except Exception,e:
            print("[insert][FAIL]DBName:%s,tableName:%s,itemDict:%s"%(DBName, tableName,itemDict))
            print(e)
            ret = False
        finally:
            cursor.close()
            return ret
    
    #根据所给参数，删除相关记录
    def delete(self, tableName, whereArgs, DBName=ORDER_TRACK_DB):
        ret = True
        param = value = ''
        try:
            initDB = "use %s"%(DBName)
            cursor = self.mConn.cursor()            
            cursor.execute(initDB)
            
            str='where'
            for key_value in whereArgs.keys():
                value=whereArgs[key_value]
                str = "%s %s='%s' and"%(str, key_value, value)
                #str=str+' '+key_value+'='+value+' '+'and'
            where=str[:-3]
            sql="delete from %s %s"%(tableName,where)
            cursor.execute(sql)
            self.mConn.commit()
        except Exception,e:
            print("[delete][FAIL]DBName:%s,tableName:%s,where:%s"%(DBName, tableName,where))
            print(e)
            ret = False
        finally:
            cursor.close()
            return ret
    
    def update(self, tableName, updateDict, whereArgs, DBName=ORDER_TRACK_DB):
        ret = True
        param = value = ''
        try:
            initDB = "use %s"%(DBName)
            cursor = self.mConn.cursor()            
            cursor.execute(initDB)
            
            str='set'
            for key_value in updateDict.keys():
                value=updateDict[key_value]
                str = "%s %s='%s' and"%(str, key_value, value)
            updateStr=str[:-3]
            
            str='where'
            for key_value in whereArgs.keys():
                value=whereArgs[key_value]
                str = "%s %s='%s' and"%(str, key_value, value)
            where=str[:-3]
            sql="update %s %s %s"%(tableName, updateStr, where)
            cursor.execute(sql)
            self.mConn.commit()
        except Exception,e:
            print("[update][FAIL]DBName:%s,tableName:%s,where:%s"%(DBName, tableName,where))
            print(e)
            ret = False
        finally:
            cursor.close()
            return ret

    def select(self, tableName, whereArgs, DBName=ORDER_TRACK_DB):
        retDictList = []
        param = value = ''
        try:
            initDB = "use %s"%(DBName)
            cursor = self.mConn.cursor()
            cursor.execute(initDB)

            str='where'
            for key_value in whereArgs.keys():
                value=whereArgs[key_value]
                str = "%s %s='%s' and"%(str, key_value, value)
            where=str[:-3]
            sql="select * from %s %s"%(tableName, where)
            cursor.execute(sql)
            retDictList = cursor.fetchall()
        except Exception,e:
            print("[select][FAIL]DBName:%s,tableName:%s,where:%s"%(DBName, tableName,where))
            print(e)
        finally:
            cursor.close()
            return retDictList

if __name__ == "__main__":
    a = MySqlDBHelperBase()
    print a.queryDataBasesList()
    itemDict = {}
    itemDict['username'] = "123456"
    itemDict['password'] = "123456"
    a.insert(USER_LIST, itemDict)
