#!/usr/bin/env bash

mvn clean package -Paot -DskipTests

rm -rf target/native
mkdir -p target/native
pushd target/native > /dev/null
jar -xvf ../app-1.0.0.jar >/dev/null 2>&1
popd > /dev/null

echo "Filtering reflect-config"
java -cp target/native/BOOT-INF/lib/*:target/test-classes com.nc.app.test.ParseReflectConfig

proxies=$(realpath native-image-agent/proxy-config.json)
resources=$(realpath native-image-agent/resource-config.json)
reflection=$(realpath native-image-agent/reflect-config-filtered.json)

pushd target/native > /dev/null
cp -R META-INF BOOT-INF/classes
native-image -H:Name=app-nat \
-H:DynamicProxyConfigurationFiles=$proxies \
-H:ReflectionConfigurationFiles=$reflection \
-cp BOOT-INF/classes:`find BOOT-INF/lib | tr '\n' ':'` \
--trace-class-initialization=org.springframework.boot.logging.logback.ColorConverter \
--initialize-at-build-time=org.springframework.boot.logging.logback.ColorConverter
mv app-nat ../
popd > /dev/null
