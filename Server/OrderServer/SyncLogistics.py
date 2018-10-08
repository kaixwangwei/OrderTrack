# -*- coding: utf-8 -*-

import OrderTrackDB
import json
from KuaiDiCX.KuaiDiChaXun import KuaiDiChaXun, KuaiDiInfo
#读取数据库中每一条物流编号，筛选出未完结的记录，然后查询相关信息


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
    
    def startQuery(self):
        record = OrderTrackDB.getAllNeedSyncLogistical()
        for item in record:
            OrderCode = ""
            ShipperCode = item.shipperCode
            LogisticCode = item.logisticCode
            a = KuaiDiChaXun()
            dictInfoStr = a.query(OrderCode, ShipperCode, LogisticCode)
            KuaiDiInfoDict = KuaiDiInfo(dictInfoStr)
            item.setLogisticsInfo(json.dumps(KuaiDiInfoDict.Traces))
            item.setLogisticsState(KuaiDiInfoDict.State)
            item.setLatestLogisticsInfo(KuaiDiInfoDict.mLastLogisticalInfo)
            ret = OrderTrackDB.updateRecord(item)
            
        
if __name__ == "__main__":
    syncLogistics = SyncLogistics()
    syncLogistics.startQuery()