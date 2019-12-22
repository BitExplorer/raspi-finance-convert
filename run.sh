#!/bin/sh

if [ $# -ne 1 ]; then
  echo "Usage: $0 <prod or local>"
  exit 1
fi
ENV=$1

APP=raspi-finance-convert
TIMEZONE='America/Chicago'
USERNAME=henninb

if [ $ENV = "prod" ]; then
  echo prod
elif [ $ENV = "local" ]; then
  echo local
else
  echo "Usage: $0 <prod or local>"
  exit 2
fi

mkdir -p .idea/runConfigurations/
mkdir -p logs
mkdir -p ssl
mkdir -p json_in
mkdir -p json_out
mkdir -p excel_in
mkdir -p config
cp -v $HOME/finance_db_master.xlsm excel_in/

cp finance_Application.xml .idea/runConfigurations/
cp TransactionServicePerf.xml .idea/runConfigurations/
git ls-files | ctags --languages=java -L-

touch config/account_credit_list.txt
touch config/account_exclude_list.txt
touch env.secrets
touch ip

HOST_BASEDIR=$(pwd)
GUEST_BASEDIR=/opt/${APP}
#HOST_IP=$(ipconfig getifaddr en0) #MacOS
HOST_IP=$(cat ip)
LOGS=$BASEDIR/logs

./gradlew clean build
if [ $? -ne 0 ]; then
  echo "gradle build failed."
  exit 1
fi
rm -rf LOGS_IS_UNDEFINED
docker build -t $APP --build-arg TIMEZONE=${TIMEZONE} --build-arg APP=${APP} --build-arg USERNAME=${USERNAME} .
if [ $? -ne 0 ]; then
  echo "docker build failed."
  exit 1
fi

echo docker run -it -h ${APP} --add-host hornsup:$HOST_IP -p 8081:8080 --env-file env.secrets --env-file env.$ENV -v $HOST_BASEDIR/logs:$GUEST_BASEDIR/logs -v $HOST_BASEDIR/ssl:$GUEST_BASEDIR/ssl -v $HOST_BASEDIR/json_in:$GUEST_BASEDIR/json_in -v $HOST_BASEDIR/config:$GUEST_BASEDIR/config -v $HOST_BASEDIR/excel_in:$GUEST_BASEDIR/excel_in --rm ${APP} bash
docker run -it -h ${APP} --add-host hornsup:$HOST_IP -p 8081:8080 --env-file env.secrets --env-file env.$ENV -v $HOST_BASEDIR/logs:$GUEST_BASEDIR/logs -v $HOST_BASEDIR/ssl:$GUEST_BASEDIR/ssl -v $HOST_BASEDIR/json_in:$GUEST_BASEDIR/json_in -v $HOST_BASEDIR/config:$GUEST_BASEDIR/config -v $HOST_BASEDIR/excel_in:$GUEST_BASEDIR/excel_in --rm --name ${APP} ${APP}

exit 0
