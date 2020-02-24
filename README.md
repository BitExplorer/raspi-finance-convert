# raspi-finance-convert

## Purpose: take input json data files of 1 or more records and insert each record into the database.
Example Record: {"guid":"246190f4-cd34-4c0e-987b-56a4c3d3958c","transactionDate":"1572933600000","description":"Subway","category":"restaurant","amount":"10.65","reoccurring":"false","cleared":"1","notes":"","dateUpdated":"1573219281000","dateAdded":"1573219281000","accountType":"credit","accountNameOwner":"chase_brian","sha256":"4add85bc7570312be62cdfe895d52bedb37a73fa6f8d3e04f3455c8fc306aa7d"}

## setup local (h2)
- assumption - docker is installed on your system; and port 8081 is available
- ./run.sh local
- Access h2 database: http://localhost:8081/h2-console/, connection string: jdbc:h2:mem:finance_db;DB_CLOSE_DELAY=-1
- cp test.json json_in

## setup prod
- assumption - postgreql database server is installed and docker is installed on your system.
- assumption - port 8081 is available.
- setup the postgresql database - run the project raspi-finance-database [https://github.com/BitExplorer/raspi_finance_database]
- In your /etc/hosts file add hornsup as the local ip address; Example: 192.168.100.25 hornsup
- create the file env.secrets and set a value for the following
```
DATASOURCE_PASSWORD=changeit
EXCEL_PASSWORD=changeit
```
- create a file called ip and put your ip address  in it. Example: 192.168.100.25
- ./run.sh prod
- cp test.json json_in

## setup intellij
configuration VM Options add the following entry
```
-Dspring.profiles.active=local
```
cat env.intellij file to get the environment details for intellij

## setup mongo
** work in progress

## setup running custom application.yml
./gradlew clean build
./gradlew bootRun
java -jar build/libs/raspi_finance*.jar --spring.config.location=src/main/resources/application-prod.yml

## update gradle wrapper version
./gradlew wrapper --gradle-version 6.0
./gradlew wrapper --gradle-version 6.0 --distribution-type all

## gradle command to find dependencies
./gradlew :dependencies > dependencies.txt
./gradlew :dependencies --configuration compile > dependencies_compile.txt

@JsonIgnoreProperties(ignoreUnknown = true)
Also for better performance:
objectMapper = new ObjectMapper();
objectMapper.registerModule(new AfterburnerModule());   // Speeds up serialization.

You need the dependency in your build.gradle:
implementation "com.fasterxml.jackson.module:jackson-module-afterburner:${jacksonVersion}"



git config --global filter.updateSecretToken.clean 'sed "s/_PASSWORD=\".*\"/_PASSWORD=\"********\"/'
