#!/bin/sh

touch env.secrets
touch env.console

set -a
. ./env.console
. ./env.secrets
set +a

./gradlew clean build bootRun

exit 0