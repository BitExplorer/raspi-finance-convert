#!/bin/sh

if [ $# -ne 1 ]; then
  echo "Usage: $0 <prod or local>"
  exit 1
fi
ENV=$1

if [ $ENV = "prod" ]; then
  echo
fi

mkdir -p logs ssl json_in
touch env.secrets
touch ip
HOST_BASEDIR=$(pwd)
GUEST_BASEDIR=/opt/raspi_finance_convert
#HOST_IP=$(ipconfig getifaddr en0) #MacOS
HOST_IP=$(cat ip)
export LOGS=$BASEDIR/logs
./gradlew clean build
rm -rf LOGS_IS_UNDEFINED
docker build -t raspi_finance_convert .
echo docker run -it -h raspi_finance_convert --add-host hornsup:$HOST_IP -p 8081:8080 --env-file env.secrets --env-file env.$ENV -v $HOST_BASEDIR/logs:$GUEST_BASEDIR/logs -v $HOST_BASEDIR/ssl:$GUEST_BASEDIR/ssl -v $HOST_BASEDIR/json_in:$GUEST_BASEDIR/json_in --rm --name raspi_finance_convert raspi_finance_convert
docker run -it -h raspi_finance_convert --add-host hornsup:$HOST_IP -p 8081:8080 --env-file env.secrets --env-file env.$ENV -v $HOST_BASEDIR/logs:$GUEST_BASEDIR/logs -v $HOST_BASEDIR/ssl:$GUEST_BASEDIR/ssl -v $HOST_BASEDIR/json_in:$GUEST_BASEDIR/json_in --rm --name raspi_finance_convert raspi_finance_convert

exit 0
