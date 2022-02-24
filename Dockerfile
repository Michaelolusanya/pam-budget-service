FROM maven:3.8.3-jdk-11-slim

RUN mkdir /budget-service
WORKDIR /budget-service

COPY /Application/target /budget-service

ENTRYPOINT java -jar --spring.profiles.active=$PROFILE /budget-service/pam-budget-service-application-0.0.1-SNAPSHOT-exec.jar