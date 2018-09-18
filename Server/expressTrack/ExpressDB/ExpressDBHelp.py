#!/usr/bin/python
# -*- coding: UTF-8 -*-


import re
import datetime
import collections
import pymysql.cursors
from ExpressDB.MySqlDef import *
from ExpressDB.MySqlDBHelperBase import MySqlDBHelperBase


class ExpressDBHelp:
    def __init__(self):
        self.mMySqlDBHelperBase = MySqlDBHelperBase()
    
    
    def verifyUser(self, username, password):
        
        tableName = USER_LIST
        whereArgs = {}
        whereArgs['username'] = username
        whereArgs['password'] = password
        resultDict = self.mMySqlDBHelperBase.select(tableName, whereArgs, DBName=ORDER_TRACK_DB)
        print resultDict
        if resultDict == [] or resultDict == None or resultDict == ()  :
            return False
        return True