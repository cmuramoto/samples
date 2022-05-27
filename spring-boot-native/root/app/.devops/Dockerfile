FROM cmuramoto/alpine-jdk:jdk.17.parallel-1.0.0 as build

# Layer 1: jlink
RUN /opt/jdk/bin/jlink \
    --add-modules java.base,java.compiler,java.datatransfer,java.desktop,java.instrument,java.logging,java.management,java.management.rmi,java.naming,java.prefs,java.scripting,java.security.jgss,java.sql,java.sql.rowset,java.transaction.xa,java.xml,jdk.httpserver,jdk.jfr,jdk.unsupported \
    --verbose \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /opt/jdk-jlink

# Layer 2: Fresh Alpine
FROM alpine:3.14

LABEL maintainer="cleber.muramoto@gmail.com"

COPY --from=build /opt/jdk-jlink /opt/jdk

# Final Layer: Setup Envs
ENV VM_OPTS_BASE="-XX:+UnlockExperimentalVMOptions -XX:+UnlockDiagnosticVMOptions --enable-preview"
ENV VM_OPTS_PANDORA_ADD=""
ENV VM_OPTS_PANDORA_OPEN="--add-opens java.management/java.lang.management=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.math=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED"
ENV VM_OPTS_PANDORA_EXPORT="--add-exports java.base/jdk.internal.misc=ALL-UNNAMED --add-exports java.base/jdk.internal.reflect=ALL-UNNAMED --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED --add-exports java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED --add-exports java.base/jdk.internal.org.objectweb.asm.util=ALL-UNNAMED --add-exports java.base/sun.nio.ch=ALL-UNNAMED --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-exports java.base/jdk.internal.util=ALL-UNNAMED"
ENV VM_OPTS_MIN_HEAP="512m"
ENV VM_OPTS_MAX_HEAP="512m"
ENV VM_OPTS_MAX_OFFHEAP="16G"
ENV VM_OPTS_CONTAINER="-XX:+UseContainerSupport -XX:+PreferContainerQuotaForCPUCount"
ENV VM_OPTS_GC="Parallel"
ENV VM_OPTS_GC_CONF="-XX:-ClassUnloading -XX:+AlwaysPreTouch"
ENV VM_OPTS_EXTRA=""
ENV VM_SYS_PROPS="-Duser.timezone=utc"
ENV APP_BIN="/usr/app/app.jar"

ENTRYPOINT exec /opt/jdk/bin/java -cp /usr/app/app.jar com.nc.app.boot.Main \
${VM_OPTS_BASE} ${VM_OPTS_PANDORA_ADD} ${VM_OPTS_PANDORA_OPEN} ${VM_OPTS_PANDORA_EXPORT} \
-Xms${VM_OPTS_MIN_HEAP} -Xmx${VM_OPTS_MAX_HEAP} -XX:MaxDirectMemorySize=${VM_OPTS_MAX_OFFHEAP} \
${VM_OPTS_CONTAINER} -XX:+Use${VM_OPTS_GC}GC ${VM_OPTS_GC_CONF} \
-XX:CompileCommandFile=${VM_OPTS_COMPILATION_FORCE_INLINE} ${VM_OPTS_COMPILATION_DIAGNOSTIC} -XX:LogFile=${VM_OPTS_COMPILATION_LOG} \
${VM_OPTS_EXTRA} $VM_SYS_PROPS -jar $APP_BIN

RUN rm -f /usr/app/app.jar

# Placed at last step since usually everything abov will be cached
COPY target/app.jar /usr/app/app.jar
