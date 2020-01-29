#!/bin/sh

touch env.secrets
touch env.console

set -a
. ./env.console
. ./env.secrets
set +a

./gradlew clean bootRun

exit 0
