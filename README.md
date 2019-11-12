# raspi-finance-convert
Purpose: take input json data files of 1 or more records and insert each record into the database.

## setup local (h2)
1) assumption - docker is installed on your system; and port 8081 is available
2) ./run.sh local
3) Access h2 database: http://localhost:8081/h2-console/, connection string: jdbc:h2:mem:finance_db;DB_CLOSE_DELAY=-1
4) cp test.json json_in

## setup prod
1) assumption - postgreql database server is installed and docker is installed on your system.
2) assumption - port 8081 is available.
3) setup the postgresql database - run the project raspi-finance-database [https://github.com/BitExplorer/raspi_finance_database]
4) In your /etc/hosts file add hornsup as the local ip address; Example: 192.168.100.25 hornsup
5) create the file env.secrets and set a value for DATASOURCE_PASSWORD. Example: DATASOURCE_PASSWORD=changeit
6) create a file called ip and put your ip address  in it. Example: 192.168.100.25
7) ./run.sh prod
8) cp test.json json_in

## setup running custom application.yml
./gradlew clean build
./gradlew bootRun
java -jar build/libs/raspi_finance*.jar --spring.config.location=src/main/resources/application.yml

## update gradle wrapper version
./gradlew wrapper --gradle-version 6.0
gradle wrapper --gradle-version 6.0 --distribution-type all

## gradle command to find dependencies
./gradlew :dependencies > dependencies.txt
./gradlew :dependencies --configuration compile > dependencies_compile.txt
