#!/bin/bash

PANDORA_ARGS=""

GC_OPTS="-XX:+UseSerialGC"

AGENT="-agentlib:native-image-agent=config-output-dir=native-image-agent/"

APP_ARGS="-DspringAot=true -Duser.timezone=UTC"

/opt/java/latest_graal_17/bin/java $PANDORA_ARGS $GC_OPTS $AGENT $APP_ARGS -jar target/app-1.0.0.jar
