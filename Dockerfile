FROM maven:3.8.3-jdk-11
RUN apt-get -o Acquire::Check-Valid-Until=false -o Acquire::Check-Date=false --allow-releaseinfo-change \
     update -y && apt-get install -y git vim systemd

RUN apt-get install -y python3 
RUN apt-get install -y python3-pip
RUN pip3 install requests

RUN mkdir -p /boilerplate/api

RUN mkdir /boilerplate/jar-file

WORKDIR /boilerplate/api

COPY . /boilerplate/api

#RUN chmod 777 -R

RUN mkdir -p /boilerplate/storage/files/

RUN mvn clean install

EXPOSE 8080

ENTRYPOINT ["java", "-jar","/boilerplate/api/target/boilerplate-0.0.1-SNAPSHOT.jar"]

