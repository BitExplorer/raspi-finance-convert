#!/bin/sh

if [ $# -ne 1 ]; then
  echo "Usage: $0 <prod or local or mongo>"
  exit 1
fi
ENV=$1

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

cp finance_Application.xml .idea/runConfigurations/
cp TransactionServicePerf.xml .idea/runConfigurations/

touch env.secrets
touch ip

HOST_BASEDIR=$(pwd)
GUEST_BASEDIR=/opt/raspi-finance-convert
#HOST_IP=$(ipconfig getifaddr en0) #MacOS
HOST_IP=$(cat ip)
export LOGS=$BASEDIR/logs
./gradlew clean build -x test
rm -rf LOGS_IS_UNDEFINED
docker build -t raspi-finance-convert .
echo docker run -it -h raspi-finance-convert --add-host hornsup:$HOST_IP -p 8081:8080 --env-file env.secrets --env-file env.$ENV -v $HOST_BASEDIR/logs:$GUEST_BASEDIR/logs -v $HOST_BASEDIR/ssl:$GUEST_BASEDIR/ssl -v $HOST_BASEDIR/json_in:$GUEST_BASEDIR/json_in --rm --name raspi-finance-convert raspi-finance-convert
docker run -it -h raspi-finance-convert --add-host hornsup:$HOST_IP -p 8081:8080 --env-file env.secrets --env-file env.$ENV -v $HOST_BASEDIR/logs:$GUEST_BASEDIR/logs -v $HOST_BASEDIR/ssl:$GUEST_BASEDIR/ssl -v $HOST_BASEDIR/json_in:$GUEST_BASEDIR/json_in --rm --name raspi-finance-convert raspi-finance-convert

exit 0
