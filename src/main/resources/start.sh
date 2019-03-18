#!/bin/sh

JAR_FILE=push-scheduler-1.0.jar
JAVA_HOME=/opt/java8/bin/IA64W
CONF_PATH=./conf
CLASS_PATH=.:./conf
LOG_FILE=./conf/logback.xml

pid=$(ps -efx | grep java | grep $JAR_FILE | awk '{print $2}')

if [ -z "$pid" ]; then
	nohup $JAVA_HOME/java -Xms4096m -Xmx4096m -cp $CLASS_PATH -DconfigPath=$CONF_PATH -Dlogback.configurationFile=$LOG_FILE -jar $JAR_FILE 1>/dev/null 2>&1 &
	echo "$JAR_FILE STARTED."
else
	echo "$JAR_FILE is running already."
fi