# -*- coding: utf-8 -*-

from collections import OrderedDict
SHIPPER_MAP = OrderedDict()
SHIPPER_MAP["SF"] = "顺丰速运"
SHIPPER_MAP["HTKY"] = "百世快递"
SHIPPER_MAP["ZTO"] = "中通快递"
SHIPPER_MAP["STO"] = "申通快递"
SHIPPER_MAP["YTO"] = "圆通速递"
SHIPPER_MAP["YD"] = "韵达速递"
SHIPPER_MAP["YZPY"] = "邮政快递包裹"
SHIPPER_MAP["EMS"] = "EMS"
SHIPPER_MAP["HHTT"] = "天天快递"
SHIPPER_MAP["JD"] = "京东快递"
SHIPPER_MAP["UC"] = "优速快递"
SHIPPER_MAP["DBL"] = "德邦快递"
SHIPPER_MAP["ZJS"] = "宅急送"
SHIPPER_MAP["TNT"] = "TNT快递"
SHIPPER_MAP["UPS"] = "UPS"
SHIPPER_MAP["DHL"] = "DHL"
SHIPPER_MAP["FEDEX"] = "FEDEX联邦(国内件）"
SHIPPER_MAP["FEDEX_GJ"] = "FEDEX联邦(国际件）"

#0-无轨迹
#1-已揽收
#2-在途中
#3-签收
#4-问题件
def getLogisticalState(state):
    if state == 0 :
        return u"无轨迹"
    elif state == 1:
        return u"已揽收"
    elif state == 2:
        return u"在途中"
    elif state == 3:
        return u"签收"
    elif state == 4:
        return u"问题件"
    else :
        return u"数据异常"