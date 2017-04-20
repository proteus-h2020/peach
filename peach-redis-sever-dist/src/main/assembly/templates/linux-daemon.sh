#!/bin/bash

### BEGIN INIT INFO
# Provides: @APP_NAME@
# Required-Start:
# Required-Stop:
# Default-Start: 2 3 4 5
# Default-Stop: 0 1 6
# Short-Description: Ingestion API
# Description: REST Ingestion API
#
### END INIT INFO

#Get the script directory
#SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
SCRIPT_DIR=$( dirname "$( readlink -f "$0")" )
APP_DIR=$( dirname ${SCRIPT_DIR})
#echo "Script directory: ${SCRIPT_DIR}"
#echo "App directory: ${APP_DIR}"

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
#echo "Classpath: ${CLASSPATH}"

JAVA_EXEC=$( which java )
JAVA_HOME=$(dirname $(dirname $(readlink -f "${JAVA_EXEC}")))

#Allow the environment script to overwrite the variables
if [ -f "${CONF}/environment.sh" ]; then
  #echo "Loading environment"
  source "${CONF}/environment.sh"
fi

if [ -z "${JAVA_HOME}" ]; then
  echo "Java executable not found"
  exit 1
fi

#echo "Daemon PID: ${DAEMON_PID}"


assert_daemon_running(){
  if [ ! -f ${DAEMON_PID} ]; then
    echo "@APP_NAME@ is not running"
    exit 5
  fi
}

assert_daemon_stop(){
  if [ -f ${DAEMON_PID} ]; then
    echo "@APP_NAME@ is already running"
    exit 6
  fi
}


exec_daemon(){
  exec "${JAVA_EXEC}" @EXTRA_JVM_ARGUMENTS@ ${JAVA_OPTS} \
    -classpath "${CLASSPATH}" \
    -Dapp.name="@APP_NAME@" \
    -Dapp.pid="$$" \
    @MAINCLASS@ \
    @APP_ARGUMENTS@"$@" > ${DAEMON_LOG} 2> ${DAEMON_ERROR_LOG} &
}

start(){
  assert_daemon_stop

  exec_daemon
  PID=$!
  LAUNCHED=$?

  if [ ${LAUNCHED} -eq 0 ]; then
    echo "Starting @APP_NAME@ . . ."
    echo ${PID} > ${DAEMON_PID}
  else
    echo "Cannot launch @APP_NAME@"
  fi
}

stop(){
  assert_daemon_running

  PID=$(cat ${DAEMON_PID})
  kill -SIGINT ${PID}
  sleep 1
  PROCESS=$(ps -Af | grep ${PID} | grep -v grep)
  if [ -z ${PROCESS} ]; then
    rm ${DAEMON_PID}
    echo "@APP_NAME@ stopped"
  else
    echo "@APP_NAME@ refuses to die"
    exit 7
  fi

}

case "$1" in
    start)
        start
    ;;
    stop)
        stop
    ;;
    status)
        assert_daemon_running
        echo "@APP_NAME@ daemon is running"
    ;;
    restart)
        assert_daemon_running
        stop
        sleep 1
        start
    ;;
    *)
      echo "Usage: /etc/init.d/${PRG} {start|stop|restart}" >&2
      exit 4
    ;;
esac
