FROM maven:3.6.3-jdk-11 AS maven
USER root
COPY ./ /tmp/code
RUN cd /tmp/code && mvn clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true


FROM openjdk:8-jre
RUN mkdir /usr/local/java
RUN mkdir /usr/local/java/docker
COPY --from=maven /tmp/code/target/*.jar /usr/local/java/webdav.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xmx1g"
ENV ALIYUNDRIVE_REFRESH_TOKEN="2121b627c07648b983dfa584d615413e"
ENV ALIYUNDRIVE_AUTH_ENABLE="true"
ENV ALIYUNDRIVE_AUTH_USERNAME="admin"
ENV ALIYUNDRIVE_AUTH_PASSWORD="admin"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /usr/local/java/webdav.jar"]
