from flask_script import Manager
from OrderTrackBase import *
import config
from main import app
from flask_migrate import Migrate,MigrateCommand
from OrderTrackRecord import RecodeList
from OrderTrackUser import User

manager = Manager(app)

migrate = Migrate(app,db)

manager.add_command('db',MigrateCommand)


if __name__ == '__main__':
    manager.run()

