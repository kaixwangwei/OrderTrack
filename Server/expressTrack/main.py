#encoding:utf-8
import os
from flask import Flask,render_template,request,flash,url_for
from ExpressDB.ExpressDBHelp import ExpressDBHelp
app = Flask(__name__)
# set the secret key.  keep this really secret:
app.secret_key = os.urandom(24)

@app.route('/', methods=['GET', 'POST'])
@app.route('/index', methods=['GET', 'POST'])
@flask_login.login_required
def index():
    logger.debug("index page, method is %s" % request.method)
    return render_template('index.html', name=flask_login.current_user.id)




@app.route("/userLogin", methods=['POST'])
def userLogin():
    jsonpay = request.json

    expressDB = ExpressDBHelp()
    username = jsonpay.get("username")
    password = jsonpay.get("password")
    print("[userLogin]username =%s, password=%s"%(username, password))
    if  not username:
        return "fail"
    if  not password:
        return "fail"
    if expressDB.verifyUser(username, password) :
        return "success"
    else:
        return "fail"


@app.route("/clientToServer", methods=['POST'])
def clientToServer():
    print("clientToServer")
    jsonpay = request.json
    print(jsonpay)

if __name__ == '__main__':
    app.run(host='0.0.0.0',debug=True)