#!/bin/sh

#macos
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home
export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home

# redhat
export JAVA_HOME=$(dirname $(dirname $(readlink $(readlink $(which javac)))))
#export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.232.b09-0.fc31.x86_64/

echo $JAVA_HOME

export PATH=${JAVA_HOME}/bin:${PATH}

if [ $# -ne 1 ]; then
  echo "Usage: $0 <prod or local>"
  exit 1
fi
ENV=$1

APP=raspi-finance-convert
TIMEZONE='America/Chicago'
USERNAME=henninb
HOST_BASEDIR=$(pwd)
GUEST_BASEDIR=/opt/${APP}

if [ $ENV = "prod" ]; then
  echo prod
elif [ $ENV = "local" ]; then
  echo local
else
  echo "Usage: $0 <prod or local>"
  exit 2
fi

# "$OSTYPE" == "darwin"*
if [ \( "$OS" = "Linux Mint" \) -o \(  "$OS" = "Ubuntu" \) -o \(  "$OS" = "Raspbian GNU/Linux" \) ]; then
  HOST_IP=$(hostname -I | awk '{print $1}')
elif [ "$OS" = "Arch Linux" ]; then
  HOST_IP=$(hostname -i | awk '{print $1}')
elif [ "$OS" = "Fedora" ]; then
  HOST_IP=192.168.100.130
elif [ "$OS" = "Darwin" ]; then
  HOST_IP=$(ipconfig getifaddr en0)
elif [ "$OS" = "Gentoo" ]; then
  HOST_IP=$(hostname -i | awk '{print $1}')
else
  echo $OS is not yet implemented.
  exit 1
fi

mkdir -p src/main/scala
mkdir -p src/main/java
mkdir -p src/main/kotlin
mkdir -p src/test/unit/groovy
mkdir -p src/test/unit/java
mkdir -p src/test/integration/groovy
mkdir -p src/test/integration/java
mkdir -p src/test/functional/groovy
mkdir -p src/test/functional/java
mkdir -p src/test/performance/groovy
mkdir -p src/test/performance/java

mkdir -p logs
mkdir -p ssl
mkdir -p json_in
mkdir -p json_out
mkdir -p excel_in
mkdir -p config
mkdir -p .idea/runConfigurations/
cp -v $HOME/finance_db_master.xlsm excel_in/

cp -v finance_Application.xml .idea/runConfigurations/
cp -v TransactionServicePerf.xml .idea/runConfigurations/
#git ls-files | ctags --language=java
#find . -name "*.java" | xargs ctags --language=java

touch config/account_credit_list.txt
touch config/account_exclude_list.txt
touch env.secrets
touch env.console

./gradlew clean build
if [ $? -ne 0 ]; then
  echo "gradle build failed."
  exit 1
fi

if [ -x "$(command -v docker)" ]; then
  docker build -t $APP --build-arg TIMEZONE=${TIMEZONE} --build-arg APP=${APP} --build-arg USERNAME=${USERNAME} .
  if [ $? -ne 0 ]; then
    echo "docker build failed."
    exit 1
  fi

  echo docker run -it -h ${APP} --add-host hornsup:$HOST_IP --env-file env.secrets --env-file env.$ENV -v $HOST_BASEDIR/logs:$GUEST_BASEDIR/logs -v $HOST_BASEDIR/ssl:$GUEST_BASEDIR/ssl -v $HOST_BASEDIR/json_in:$GUEST_BASEDIR/json_in -v $HOST_BASEDIR/config:$GUEST_BASEDIR/config -v $HOST_BASEDIR/excel_in:$GUEST_BASEDIR/excel_in --rm ${APP} bash
  docker run -it -h ${APP} --add-host hornsup:$HOST_IP -p 8082:8080 --env-file env.secrets --env-file env.$ENV -v $HOST_BASEDIR/logs:$GUEST_BASEDIR/logs -v $HOST_BASEDIR/ssl:$GUEST_BASEDIR/ssl -v $HOST_BASEDIR/json_in:$GUEST_BASEDIR/json_in -v $HOST_BASEDIR/config:$GUEST_BASEDIR/config -v $HOST_BASEDIR/excel_in:$GUEST_BASEDIR/excel_in --rm --name ${APP} ${APP}
  if [ $? -ne 0 ]; then
    echo "docker run failed."
    exit 1
  fi
else
  set -a
  . /env.secrets
  . ./env.console
  set +a
  ./gradlew clean build bootRun
fi

exit 0
