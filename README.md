## Setup on Ubuntu
### Clone the repository
- `$ git clone https://github.com/LeBoBar/TDB.git`

### Install development packages
- `$ sudo apt-get install openjdk-6-jre`
- `$ sudo apt-get install ant`

### Install Eclipse
- `$ sudo apt-get install eclipse`

### Run make to compile .jar
- `$ make`

### Copy the database
- Find a copy of the datase, create a tdb database and import data

### Configure TDB.config
You should have a TDB.config file in src/ containing:
- trigrammeBanque=BOB
- dbUser=\<user for database>
- dbPassword=\<password>
- dbHost=localhost
- dbName=tdb
