# -*- coding: utf-8 -*-


import json
import time

import OrderTrackDB
from KuaiDiCX.KuaiDiChaXun import KuaiDiChaXun, KuaiDiInfo
#读取数据库中每一条物流编号，筛选出未完结的记录，然后查询相关信息


def startQueryAllLogistical():
    print("startQueryAllLogistical")
    syncLogistics = SyncLogistics()
    syncLogistics.startQuery()

def startQueryLogistical(logisticsCode):
    print("startQueryLogistical:%s"%(logisticsCode))
    syncLogistics = SyncLogistics()
    syncLogistics.startQuery(logisticsCode)
    
def Singleton(cls):
    _instance = {}

    def _singleton(*args, **kargs):
        if cls not in _instance:
            _instance[cls] = cls(*args, **kargs)
        return _instance[cls]

    return _singleton


@Singleton
class SyncLogistics:
    def __init__(self):
        pass
    
    def getCurrentTime(self):
        localTime = time.localtime()
        strTime = time.strftime("%Y-%m-%d %H:%M:%S", localTime)
        return strTime
    
    def startQuery(self, logisticsCode = None):
        if logisticsCode == None:
            record = OrderTrackDB.getAllNeedSyncLogistical()
        else :
            record = OrderTrackDB.getNeedSyncLogistical(logisticsCode)
            
        for item in record:
            OrderCode = ""
            ShipperCode = item.shipperCode
            logisticsCode = item.logisticsCode
            a = KuaiDiChaXun()
            dictInfoStr = a.query(OrderCode, ShipperCode, logisticsCode)
            KuaiDiInfoDict = KuaiDiInfo(dictInfoStr)
            item.setLogisticsInfo(json.dumps(KuaiDiInfoDict.Traces))
            item.setLogisticsState(KuaiDiInfoDict.State)
            item.setLatestLogisticsInfo(KuaiDiInfoDict.mLastLogisticalInfo)
            item.setLogisticsUpdateTime(self.getCurrentTime())
            #print("LogisticsUpdateTime = %s"%(item.logisticsUpdateTime))
            ret = OrderTrackDB.updateRecord(item)
            
            
                    
if __name__ == "__main__":
    syncLogistics = SyncLogistics()
    syncLogistics.startQuery()