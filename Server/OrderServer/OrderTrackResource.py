# -*- coding: utf-8 -*-
from flask import Blueprint

ordertrack_resource = Blueprint('ordertrack_resource', __name__, static_folder='static',
                             template_folder='templates')
