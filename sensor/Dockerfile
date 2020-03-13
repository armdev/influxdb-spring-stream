FROM amazoncorretto
COPY target/sensor.jar /opt/sensor.jar
ENTRYPOINT ["java","-Xmx1024m", "-Xss4096m", "-Xmx4096m", "-jar","/opt/sensor.jar"]
EXPOSE 2075
