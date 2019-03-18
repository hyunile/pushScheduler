#!/bin/sh

PORT=8481
JAR_FILE=push-scheduler-1.0.jar

pid=$(ps -efx | grep java | grep $JAR_FILE | awk '{print $2}')

if ! [ -z "$pid" ]; then
#    echo "Waiting until queue is empty ..."
#    url="http://127.0.0.1:$PORT/shutdown"
#    curl $url
	echo "Stopping $JAR_FILE ..."
	kill -15 $pid
	while kill -0 $pid 1>/dev/null 2>&1
	do
		sleep 1
	done
	echo "$JAR_FILE STOPPED."
else
	echo "$JAR_FILE is not running."
fi