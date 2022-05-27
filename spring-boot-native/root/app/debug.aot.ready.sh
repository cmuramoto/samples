#!/bin/bash

PANDORA_ARGS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9999"

GC_OPTS="-XX:+UseSerialGC"

APP_ARGS="-DspringAot=true -Duser.timezone=UTC -Dhibernate.show_sql=false"

/opt/java/latest_graal_17/bin/java $PANDORA_ARGS $GC_OPTS $AGENT $APP_ARGS -jar target/app-1.0.0.jar
