#encoding:utf-8

from flask import Flask,render_template,request,flash,url_for
app = Flask(__name__)
app.secret_key = "he234zse"


@app.route("/userLogin", methods=['POST'])
def userLogin():
    form = request.form
    print type(form)
    print form.getlist('username')
    
    print form
    forDict = form.to_dict()
    print forDict
    username = forDict.get("username")
    password = forDict.get("password")
    print("[userLogin]username = %s, password=%s"%(username, password))
    if  not username:
        return "fail"
    if  not password:
        return "fail"
    if username=='123456' and password=='123456':
        flash("ok!")
        return "success"
    else:
        return "fail"



if __name__ == '__main__':
    app.run(host='0.0.0.0',debug=True)