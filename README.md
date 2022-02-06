# pam-budget-service
To be able to run the application properly you need to have all this installed
- An IDE (Intellij recommended)
- Java 
- Docker

Download the project to your computer and follow the steps below on 
how to set everything up in different environments. You can either choose to run the boilerplate locally with containers
or through Intellij.

<br>
<br>

## How to run application locally in container mode
To run the project in container mode, first check that docker is installed on your computer.
When that is set up do the following steps.
1. Open a cmd on your computer and type in cd "path to the project folder".
2. docker-compose build (builds the project inside a container)
3. docker-compose up (Starts up the container that were built)
4. docker-compose down (removes the containers)
When the container is up the url are ` http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config `
to use the swagger (open-api documentation) and test the different endpoints. Further info about swagger can be find below
under the "Swagger" section.

<br>
<br>

## How to run application in development mode
Navigate to the path where your boilerplate-java-springboot2 code is located.
Download the project and open it inside the IDE. Intellij is recommended due to it has worked the best
together with the boilerplate. A database needs to be set up and running, either through container or through a program.
Further info below.


### Intellij (Recommended) :sparkles:
Check if the IDE recognize Spring boot and Maven. If it does skip to point 3. Otherwise follow all steps below.
1. Right click on the pom.xml file.
2. Press generate maven project.
3. Add the .env file, follow the section below about environment variables.
4. Set up database run following command:

    ```
    docker run -p 21539:5432 --name boilerplate-postgres-dev-db -d -e POSTGRES_PASSWORD=<password> -e POSTGRES_USER=username -e POSTGRES_DB=boilerplate-postgres-dev-db postgres:13.1-alpine
    ```
    OR 

    create a database by using an installed software. More information below under the section "Database".

5. Start the project by running the main class (StartSpringbootApi). 

<br>
<br>

## Database
The database can either be running locally through the terminal using containers or through some installed program.
In this project the program pgAdmin have been used and worked without any problems, so it's recommended to use. 
<br>
To run it through pgAdmin:
1. Install and start up the program
2. Add username and password to match -env datasource_username and datasource_password.
3. Add a new database the match the database-name set in the end of the datasource_url.
4. Run the springboot and all the tables should be added to the specified db inside pgAdmin.

<br>
<br>

## Environmental variables :closed_lock_with_key:
Important environment variables and how to set them up to get everything running.


### .env file
The env file declares default environment variables that are used for the docker compose environment. It should be created  
inside the current working folder ("/api"). Important that the file is named .env.
When the .env file is created, it should be containing the follow fields
Mandatory fields<br>

```
CLIENT_SCOPE_ID=
CLIENT_ID=…
TENANT_ID=…
DATASOURCE_URL=jdbc:postgresql://localhost:21539/boilerplate-postgres-dev-db
DATASOURCE_USERNAME=username
DATASOURCE_PASSWORD=<password>
```
Fields that can be added<br>
```
APPLICATION_OWNER=owner-email
BOILERPLATE_JAVA_MANAGER=https://boilerplate-java-manager.azurewebsites.net/apidocs
VERSION=0.1
ENVIRONMENT=DEVELOPMENT
```

The mandatory fields are credentials for both the client to connect for the auth, to be able to get access and use the endpoints. 
The postgres fields are defined to match the credentials the were set during the creation of the database, so that the IDE can connect with the database.


### Env-class
The `Env.java` manages static variables that are used across the application, as environment details, versioning, etc. 

<br>
<br>

## Swagger
Swagger includes automated documentation of the restful APIs expressed using json. It displays all the endpoints in a project and generates test-cases for those. To be able to test the endpoints, firstly an authorization is needed through OAuth2. After that all endpoints can be tested out. 
<br> 
The site displays which environment that the project are currently running on under the title "Environment:".
When the application is running swagger can be find with this url `http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config`
The configuration file for swagger is in **apiconfiguration/OpenapiConfiguration.java**
