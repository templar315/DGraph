FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8080
RUN sudo apt update -y && sudo apt install maven
RUN mvn -B package
ARG JAR_FILE=target/DGraph-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} springbootdocker.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/springbootdocker.jar"]