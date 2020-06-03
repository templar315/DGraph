FROM maven:3.6.3-jdk-8
COPY . /app
WORKDIR /app
RUN mvn -B package
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","target/DGraph-1.0-SNAPSHOT.jar"]