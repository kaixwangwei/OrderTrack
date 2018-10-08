
host = '127.0.0.1',
user = 'OrderTrack',
password = 'wangweiLxl'
port = 3306
database = 'OrderTrackDB'
charset = 'utf8'
ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])
UPLOAD_PATH = 'upload'
UPLOADS_DEFAULT_DEST = 'upload'
UPLOADS_DEFAULT_URL = 'http://127.0.0.1:9000/'
DIALECT = 'mysql'
DRIVER = 'mysqldb'

SQLALCHEMY_DATABASE_URI = 'mysql+mysqlconnector://OrderTrack:wangweiLxl@127.0.0.1:3306/OrderTrackDB'

SQLALCHEMY_TRACK_MODIFICATIONS = False
