FROM maven:3.8.3-openjdk-17-slim

RUN mkdir /budget-service
WORKDIR /budget-service

COPY /Application/target /budget-service

ENTRYPOINT java -Dspring.profiles.active=$PROFILE -jar /budget-service/pam-budget-service-exec.jar