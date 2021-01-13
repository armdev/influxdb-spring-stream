FROM registry.gitlab.com/armdev/dockers/openjdk15
COPY target/iot.jar /opt/iot.jar
RUN useradd -ms /bin/sh mphic2050 \
    && echo "root:Docker!" | chpasswd \
    && chown -R mphic2050 /opt/
USER mphic2050
ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:MinHeapSize=512M", "-XX:InitialHeapSize=1G", "-XX:MaxHeapSize=2G", \
            "-XX:ParallelGCThreads=100", "-XX:ConcGCThreads=30", \
            "-XX:SoftRefLRUPolicyMSPerMB=0","-XX:ZCollectionInterval=30", "-XX:ZFragmentationLimit=10", \
            "-XX:MaxGCPauseMillis=5", "-XX:+UseCompressedClassPointers", "-XX:-UseCompressedOops", "-XX:ZUncommitDelay=30", \
            "-XX:InitiatingHeapOccupancyPercent=35", "-XX:G1HeapRegionSize=16M", \
            "-XX:MinMetaspaceFreeRatio=50", "-XX:MaxMetaspaceFreeRatio=100",  \
            "-XX:+UnlockExperimentalVMOptions", "-XshowSettings:vm", "-jar","/opt/iot.jar"]
EXPOSE 2076



