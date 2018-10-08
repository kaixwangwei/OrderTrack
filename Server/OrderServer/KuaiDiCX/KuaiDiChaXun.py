# -*- coding: utf-8 -*-


import json
import base64
import requests
import hashlib
from Utils import utf8
from collections import OrderedDict

class KuaiDiChaXun:
    def __init__(self):
        self.mServerURL = "http://api.kdniao.cc/Ebusiness/EbusinessOrderHandle.aspx"
        self.mUserID = "1389006"
        self.mAPIKey = "2442a635-ea8d-413a-90e3-2487cfac85ef"
        self.mDataType = "2"
        self.mRequestType = 1002

    def __sign(self, data, app_key):
        """
        sign
        b64(md5(REQUEST_DATA))
        """
        m = hashlib.md5()
        s = utf8(data + app_key)
        m.update(s)
        digest = utf8(m.hexdigest())
        b64_str = base64.b64encode(digest).decode()
        return b64_str

    def _prepare_req_params(self, req_type, data):
        """
        :param str req_type:
        :param dict data:
        :return:
        """
        json_data = json.dumps(data, sort_keys=True)
        sign = self.__sign(json_data, self.mAPIKey)
        req_params = {
            "RequestData": json_data,
            "EBusinessID": str(self.mUserID),
            "RequestType": str(req_type),  # "1002"
            "DataSign": sign,
            "DataType": str(2),
        }
        return req_params


    def query(self, OrderCode, ShipperCode, LogisticCode):
        print("Query : %s - %s - %s"%(OrderCode, ShipperCode, LogisticCode))
        
        payLoad = {}
        payLoad['OrderCode'] = ""
        payLoad['ShipperCode'] = ShipperCode
        payLoad['LogisticCode'] = LogisticCode        

        param = self._prepare_req_params(self.mRequestType, payLoad)
        #print param
        
        headers = {'content-type': "application/x-www-form-urlencoded;charset=utf-8"}
        
        response = requests.post(self.mServerURL, data = param, headers = headers)
        #print response.text
        return response.text


#名称        - 类型(字符长度) - 是否必须 - 描述
#EBusinessID - String(10) - R - 用户 ID
#OrderCode   - String(30) - O - 订单编号
#ShipperCode - String(10) - R - 快递公司编码
#LogisticCode- String(30) - R - 快递单号
#Success     - Bool(10)   - R - 成功与否(true/false)
#Reason      - String(50) - O - 失败原因
#State       - String(5)  - R - 物 流状态 ：
#0-无轨迹
#1-已揽收
#2-在途中
#3-签收
#4-问题件
#Traces.AcceptTime    - Date - R - 轨迹发生时间
#Traces.AcceptStation - String(100) - R - 轨迹描述


#根据给定的信息解析快递详情
class KuaiDiInfo:
    def __init__(self, kuaiDiInfoStr):
        #print kuaiDiInfoStr
        dictInfo = json.loads(kuaiDiInfoStr.replace("\n", ""))
        
        #print dictInfo
        self.EBusinessID = dictInfo["EBusinessID"]
        if dictInfo.has_key("OrderCode"):
            self.OrderCode = dictInfo["OrderCode"]
        else:
            self.OrderCode = None
        self.ShipperCode = dictInfo["ShipperCode"]
        self.LogisticCode = dictInfo["LogisticCode"]
        self.Success = dictInfo["Success"]
        if dictInfo.has_key("Reason"):
            self.Reason = dictInfo["Reason"]
        else:
            self.Reason = None
        self.State = dictInfo["State"]
        
        self.mLastLogisticalInfo = ""
        self.Traces = []
        for item in dictInfo["Traces"]:
            traceDict = {}
            traceDict["AcceptStation"] = item["AcceptStation"]
            traceDict["AcceptTime"] = item["AcceptTime"]
            self.Traces.append(traceDict)
            
            self.mLastLogisticalInfo = "%s-%s"%(item["AcceptTime"], item["AcceptStation"])
    
if __name__ == "__main__":
    a = KuaiDiChaXun()
    OrderCode = ""
    ShipperCode = "YTO"
    LogisticCode = "801863855777638503"
    dictInfo = a.query(OrderCode, ShipperCode, LogisticCode)
    KuaiDiInfo = KuaiDiInfo(dictInfo)
    for item in  KuaiDiInfo.Traces:
        print item["AcceptStation"]