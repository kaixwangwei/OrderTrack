# -*- coding: utf-8 -*-
from flask import Blueprint, render_template, jsonify
import OrderTrackLogger
import time

# https://spacewander.github.io/explore-flask-zh/7-blueprints.html
# http://flask.pocoo.org/docs/0.12/blueprints/
admin = Blueprint('admin', __name__, template_folder='admin')

logger = OrderTrackLogger.get_logger(__name__)


@admin.route('/admin/<page>')
def admin_url(page):
    logger.debug('admin page is %s' % page)
    return render_template('/admin/%s.html' % page)


@admin.route('/admin/<int:num>')
def get(num):
    logger.debug('get method is %s' % num)
    return jsonify({'value': num + 1, 'timestamp': time.time()})


@admin.route('/admin/getdata')
def getdata():
    return '{"code":0,"msg":"","count":1000,"data":[{"express_code":10000,"receiver":"user-0","express_date":"2018-08-09","sender":"用户-0","current_status":"签名-0","experience":255,"joinTime":"2018-08-09","wealth":82830700,"classify":"作家","express_money":57},{"express_code":10001,"receiver":"user-1","express_date":"男","sender":"用户-1","current_status":"签名-1","experience":884,"joinTime":58,"wealth":64928690,"classify":"词人","express_money":27},{"express_code":10002,"receiver":"user-2","express_date":"女","sender":"用户-2","current_status":"签名-2","experience":650,"joinTime":77,"wealth":6298078,"classify":"酱油","express_money":31},{"express_code":10003,"receiver":"user-3","express_date":"女","sender":"用户-3","current_status":"签名-3","experience":362,"joinTime":157,"wealth":37117017,"classify":"诗人","express_money":68},{"express_code":10004,"receiver":"user-4","express_date":"男","sender":"用户-4","current_status":"签名-4","experience":807,"joinTime":51,"wealth":76263262,"classify":"作家","express_money":6},{"express_code":10005,"receiver":"user-5","express_date":"女","sender":"用户-5","current_status":"签名-5","experience":173,"joinTime":68,"wealth":60344147,"classify":"作家","express_money":87},{"express_code":10006,"receiver":"user-6","express_date":"女","sender":"用户-6","current_status":"签名-6","experience":982,"joinTime":37,"wealth":57768166,"classify":"作家","express_money":34},{"express_code":10007,"receiver":"user-7","express_date":"男","sender":"用户-7","current_status":"签名-7","experience":727,"joinTime":150,"wealth":82030578,"classify":"作家","express_money":28},{"express_code":10008,"receiver":"user-8","express_date":"男","sender":"用户-8","current_status":"签名-8","experience":951,"joinTime":133,"wealth":16503371,"classify":"词人","express_money":14},{"express_code":10009,"receiver":"user-9","express_date":"女","sender":"用户-9","current_status":"签名-9","experience":484,"joinTime":25,"wealth":86801934,"classify":"词人","express_money":75}]}'