#!/bin/bash

/opt/java/latest_graal_17/bin/native-image \
-cp target/*:target/lib/* \
-J-Xmx4G \
-J--enable-preview \
-J--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED \
-J--add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
-J--add-opens=java.base/jdk.internal.ref=ALL-UNNAMED \
--verbose --allow-incomplete-classpath \
-Dspring.native.remove-yaml-support=true \
-Dspring.native.remove-xml-support=true \
-Dspring.native.remove-spel-support=true \
-Dspring.native.remove-jmx-support=true \
--initialize-at-build-time=org.springframework.util.unit.DataSize \
-H:Class=com.nc.app.boot.Main \
-H:Name=movie-app \
-H:EnableURLProtocols=http \
--trace-class-initialization=org.springframework.util.unit.DataSize -H:+ReportExceptionStackTraces


# /opt/java/latest_graal_17/bin/native-image -cp target/app.jar -J-Xmx4G -J--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED -J--add-opens=java.base/sun.nio.ch=ALL-UNNAMED -J--add-opens=java.base/jdk.internal.ref=ALL-UNNAMED -Dspring.native.remove-unused-autoconfig=true -Dspring.native.remove-yaml-support=true --verbose --no-fallback -H:Class=com.nc.app.boot.Main -H:Name=movie-app

