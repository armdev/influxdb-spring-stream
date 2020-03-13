FROM amazoncorretto
COPY target/iot.jar /opt/iot.jar
ENTRYPOINT ["java","-Xmx1024m", "-Xss4096m", "-Xmx4096m", "-jar","/opt/iot.jar"]
EXPOSE 2076
