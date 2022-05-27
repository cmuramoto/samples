#!/bin/bash

if [[ ! -f ../target/app.jar ]]; then

  pushd ../ >> /dev/null

  mvn clean package -Pshade -DskipTests

  popd  >> /dev/null

fi


PANDORA_ARGS=""

GC_OPTS="-XX:+UseSerialGC"

AGENT="-agentlib:native-image-agent=config-output-dir=native-image-config"

APP_ARGS="-Duser.timezone=UTC"

/opt/java/latest_graal_17/bin/java $PANDORA_ARGS $GC_OPTS $AGENT $APP_ARGS -cp ../target/app.jar com.nc.app.boot.Main
