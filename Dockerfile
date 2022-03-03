FROM maven:3.8.3-jdk-11-slim

RUN mkdir /budget-service
WORKDIR /budget-service

COPY /Application/target /budget-service

ENTRYPOINT java -Dspring.profiles.active=$PROFILE -jar /budget-service/pam-budget-service-application-exec.jar