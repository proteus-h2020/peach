#!/bin/bash

#Get the script directory
#SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
SCRIPT_DIR=$( dirname "$( readlink -f "$0")" )
APP_DIR=$( dirname ${SCRIPT_DIR})
echo "Script directory: ${SCRIPT_DIR}"
echo "App directory: ${APP_DIR}"

#Configuration directory
if [ -z "${CONF}" ]; then
  CONF="${APP_DIR}/conf"
fi

# Add the libs to the classpath
LIB="${APP_DIR}/lib"
LIB_CLASSPATH=""
for jar in $(ls ${LIB}); do
  LIB_CLASSPATH=${LIB_CLASSPATH}:${LIB}/${jar}
done

#Execution classpath
CLASSPATH=${CLASSPATH}:${CONF}/${LIB_CLASSPATH}
echo "Classpath: ${CLASSPATH}"

#Allow the environment script to overwrite the variables
if [ -z "${CONF}/environment.sh" ]; then
  source "${CONF}/environment.sh"
fi

JAVA_EXEC=$( which java)

if [ -z "${JAVA_EXEC}" ]; then
  echo "Java executable not found"
  exit 1
fi

exec "${JAVA_EXEC}" @EXTRA_JVM_ARGUMENTS@ ${JAVA_OPTS} \
  -classpath "${CLASSPATH}" \
  -Dapp.name="@APP_NAME@" \
  -Dapp.pid="$$" \
  @MAINCLASS@ \
  @APP_ARGUMENTS@"$@"@UNIX_BACKGROUND@
