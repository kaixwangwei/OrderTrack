from flask_script import Manager

#python manage.py db init
#python manage.py db migrate -m "initial migrateion"
#python manage.py db upgrade


import OrderConfig
from main import app

from models.Base import db
from models.LogisticalInfo import LogisticalInfo
from models.User import User

from flask_migrate import Migrate,MigrateCommand

manager = Manager(app)

migrate = Migrate(app,db)

manager.add_command('db',MigrateCommand)


if __name__ == '__main__':
    manager.run()

