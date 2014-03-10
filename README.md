## Setup on Ubuntu
### Clone the repository
- `$ git clone https://github.com/bobar/TDB.git`

### Install development packages
- `$ sudo apt-get install openjdk-7-jre`
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


## Setup on Mac
### Clone the repository
- `$ git clone https://github.com/bobar/TDB.git`

### Install development packages
- `$ brew install ant`
- Download openjdk from http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html
- Install it
- ``` $ echo "export JAVA_HOME=`/usr/libexec/java_home`" >> ~/.profile ```
- `$ . ~/.profile`

### Run make to compile .jar
- `$ make`

### Configure TDB.config
You should have a TDB.config file in src/ containing:
- trigrammeBanque=BOB
- dbUser=\<user for database>
- dbPassword=\<password>
- dbHost=localhost
- dbName=tdb

### Copy the database
- Find a copy of the datase, create a tdb database and import data

### Install Eclipse
- Change encoding to UTF-8 (Eclipse/Préférences/Général/Workspace)
