FROM maven:3.6.3-jdk-8
VOLUME /tmp
EXPOSE 8080
RUN mvn -B package
ARG JAR_FILE=target/DGraph-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} springbootdocker.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/springbootdocker.jar"]