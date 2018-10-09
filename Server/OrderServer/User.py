# -*- coding: utf-8 -*-

import time
import json
from flask import Blueprint, render_template, jsonify, send_from_directory
from flask import Flask, redirect, url_for, request, make_response, abort
import flask_login

from models.User import User
import OrderTrackLogger
import OrderTrackDB
import OrderTrackLogger

# http://www.pythondoc.com/flask-login/index.html#request-loader
# http://docs.jinkan.org/docs/flask/index.html
# http://flask-login.readthedocs.io/en/latest/#login-example
class User(flask_login.UserMixin):
    def __init__(self):
        self.role = object
        self.fullname = None