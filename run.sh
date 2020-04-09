#!/usr/bin/env bash

if [ "$OSTYPE" = "linux-gnu" ]; then
  #export JAVA_HOME=$(dirname $(dirname $(readlink $(readlink $(which javac)))))
  JAVA_HOME=$(dirname "$(dirname "$(readlink -f "$(readlink -f "$(command -v javac)")" || readlink -f "$(command -v javac)")")")
else
  # macos
  JAVA_HOME=$(/usr/libexec/java_home)
  #export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home
fi

export JAVA_HOME
export PATH=${JAVA_HOME}/bin:${PATH}

if [ $# -ne 1 ]; then
  echo "Usage: $0 <prod or local>"
  exit 1
fi
ENV=$1

APP=raspi-finance-convert
# TIMEZONE='America/Chicago'
# USERNAME=henninb
# HOST_BASEDIR=$(pwd)
# GUEST_BASEDIR=/opt/${APP}

if [ "$ENV" = "prod" ]; then
  echo prod
elif [ "$ENV" = "local" ]; then
  echo local
else
  echo "Usage: $0 <prod or local>"
  exit 2
fi

# "$OSTYPE" == "darwin"*
if [ "$OS" = "Linux Mint" ] || [ "$OS" = "Ubuntu" ] || [ "$OS" = "Raspbian GNU/Linux" ]; then
  HOST_IP=$(hostname -I | awk '{print $1}')
elif [ "$OS" = "Arch Linux" ]; then
  HOST_IP=$(hostname -i | awk '{print $1}')
elif [ "$OS" = "openSUSE Tumbleweed" ]; then
  HOST_IP=192.168.100.193
elif [ "$OS" = "Fedora" ]; then
  HOST_IP=192.168.100.130
elif [ "$OS" = "Darwin" ]; then
  HOST_IP=$(ipconfig getifaddr en0)
elif [ "$OS" = "void" ]; then
  HOST_IP=127.0.0.1
elif [ "$OS" = "Gentoo" ]; then
  HOST_IP=$(hostname -i | awk '{print $1}')
else
  echo "$OS is not yet implemented."
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
cp -v "$HOME/finance_db_master.xlsm" excel_in/

cp -v finance_Application.xml .idea/runConfigurations/
cp -v TransactionServicePerf.xml .idea/runConfigurations/
#git ls-files | ctags --language=java
#find . -name "*.java" | xargs ctags --language=java

touch config/account_credit_list.txt
touch config/account_exclude_list.txt
touch env.secrets
touch env.console

chmod +x gradle/wrapper/gradle-wrapper.jar

if ! ./gradlew clean build ; then
  echo "gradle build failed."
  exit 1
fi

echo docker run -it -h influxdb-server --net=raspi-bridge -p 8086:8086 --rm --name influxdb-server -d influxdb
echo curl -i -XPOST http://localhost:8086/query -u "henninb:monday1" --data-urlencode "q=CREATE DATABASE metrics"

echo curl -G 'http://localhost:8086/query?pretty=true' --data-urlencode "db=metrics" -u "henninb:monday1" --data-urlencode "q=SELECT \"value\" FROM \"stuff\""

echo curl -G 'http://localhost:8086/query?pretty=true' --data-urlencode "db=metrics" -u "henninb:monday1" --data-urlencode "q=SHOW SERIES ON metrics"

echo curl -G 'http://localhost:8086/query?pretty=true' --data-urlencode "db=metrics" -u "henninb:monday1" --data-urlencode "q=SHOW measurements on metrics"

if [ -x "$(command -v docker-compose)" ]; then
  if ! docker-compose -f docker-compose.yml -f "docker-compose-${ENV}.yml" build; then
    echo "docker-compose build failed."
    exit 1
  fi
  if ! docker-compose -f docker-compose.yml -f "docker-compose-${ENV}.yml" up; then
    echo "docker-compose up failed."
    exit 1
  fi
else
  set -a
  # shellcheck disable=SC1091
  source env.console
  # shellcheck disable=SC1091
  source env.secrets
  set +a

  ./gradlew clean build bootRun
fi

exit 0
