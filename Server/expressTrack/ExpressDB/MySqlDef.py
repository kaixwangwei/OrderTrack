#!/usr/bin/python
# -*- coding: UTF-8 -*-

import collections

#创建mysql用户 , 并创建库， 并赋予相应权限
#create user 'OrderTrack'@'%' identified by '123456';
#create database OrderTrack default character set utf8 collate utf8_general_ci;
#grant all privileges on `OrderTrack`.* to 'OrderTrack'@'%' identified by '123456';

#insert into UserList values(`123456`,`123456`);


#MYSQL 地址
MYSQL_ADDR = "127.0.0.1" #132.232.23.28

#MYSQL 端口
MYSQL_PORT = ""

#MYSQL 用户名
MYSQL_USERNAME = "OrderTrack"

#MYSQL 密码
MYSQL_PASSWD = "123456"


#DB名称
ORDER_TRACK_DB = "OrderTrack"

#Table名称
USER_LIST = "UserList"
EXPRESS_LIST = "ExpressList"

CURRENCY_CODE = 'CURRENCY_CODE'
CURRENCY_NAME = 'CURRENCY_NAME'


#所有的数据表都通过 DATABASE_TABLES 来定义， 且只有一个 database
DATABASE_TABLES = collections.OrderedDict()
DATABASE_TABLES[ORDER_TRACK_DB] = ("""
CREATE TABLE IF NOT EXISTS `UserList` (
`id`  INTEGER UNSIGNED AUTO_INCREMENT,
`username`  VARCHAR(256) NOT NULL,
`password`  VARCHAR(256) NOT NULL,
PRIMARY KEY ( `id` ),
UNIQUE KEY `currency_code_unique` (`username`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
""","""
CREATE TABLE IF NOT EXISTS `ExpressList` (
`id`  INTEGER UNSIGNED AUTO_INCREMENT,
`express_code` VARCHAR(64) NOT NULL,
PRIMARY KEY ( `id` ),
UNIQUE KEY `exchange_rate_unique` (`express_code`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
""",
)