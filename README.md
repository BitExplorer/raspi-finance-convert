# raspi-finance-convert

## setup
run the project raspi-finance-database
in the /etc/hosts file add hornsup as the local ip address

## offline
SPRING_PROFILES_ACTIVE=offline
DATASOURCE=jdbc:h2:mem:finance_db;DB_CLOSE_DELAY\=-1
DATASOURCE_USERNAME=henninb
DATASOURCE_PASSWORD=monday1
DATASOURCE_DRIVER=org.h2.Driver
JSON_FILES_INPUT_PATH=C:\usr\finance_data\json_in
SERVER_PORT=8080
AMQ_BROKER_URL=ssl://hornsup:61617
AMQ_USERNAME=
AMQ_PASSWORD=
AMQ_IN_MEMORY=false
AMQ_POOLED=true
AMQ_HOSTNAME=hornsup
AMQ_PORTNUMBER=61617
AMQ_SCHEME=ssl
SSL_TRUSTSTORE=amq-client_hornsup.ts
SSL_TRUSTSTORE_PASSOWRD=monday1
SSL_KEYSTORE=amq-client_hornsup.ks
SSL_KEYSTORE_PASSOWRD=monday1
LOGS=logs
MONGO_DATABASE=finance_db
MONGO_HOSTNAME=192.168.100.25
MONGO_PORT=27017
MONGO_URI=mongodb://192.168.100.25/finance_db
ACTIVEMQ_SSL_BEANS_ENABLED=true
ACTIVEMQ_NONSSL_BEANS_ENABLED=false
DATABASE_PLATFORM=h2
HIBERNATE_DDL=update
ACTIVEMQ_SSL_ENABLE=true
CAMEL_ROUTE_ENABLED=true


gradle bootRun
java -jar build/libs/raspi_finance*.jar --spring.config.location=src/main/resources/application.properties
java -jar build/libs/raspi_finance*.jar --spring.config.location=src/main/resources/application.home.properties
java -jar target/raspi_finance*.jar --spring.config.location=src/main/resources/application.home.properties

IntelliJ
Setting the VM Options with -Dspring.profiles.active=work -Dspring.config.location=application.work.properties
Setting the VM Options with -Dspring.profiles.active=offline

mvn package
gradle build

??? spring.datasource.platform=h2
https://www.thomasvitale.com/https-spring-boot-ssl-certificate/

gradle dependencyInsight --dependency slf4j-log4j12
gradle dependencyInsight --dependency log4j-over-slf4j
gradle dependencyInsight --dependency logback-classic
gradle dependencies

openssl s_client -connect 192.168.100.25:8080 -CAfile /path/to/cert/crt.pem

./gradlew wrapper --gradle-version 4.10

