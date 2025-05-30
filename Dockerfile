FROM openjdk:22-jdk-slim
ARG JAR_FILE=target/BankAPI-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app

COPY docs docs
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]