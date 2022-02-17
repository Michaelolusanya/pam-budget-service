# pam-budget-service
To be able to run the application properly you need to have all this installed
- An IDE (Intellij recommended)
- Java 11
- Docker

Download the project to your computer and follow the steps below on 
how to set everything up.

## How to run&debug application in local environment
Navigate to the root of pam-budget-service **mvn clean install**, this will create everything that pam-budget-service
need itself.

To debug connect a *Remote JVM Debug* configuration on port **5006**

### Add project to Intellij (Recommended)
1. git clone git@github.com:icomdev/pam-budget-service.git
2. IntelliJ -> New -> Project from Existing Sources...
3. Select the root pom

## Database
### Local
pam-budget-service uses the H2 engine to run an embedded Postgres db in local and its pipelines.

To view the database:
1. Start application
2. localhost:23154/h2-console
* User name: sa
* JDBC URL: jdbc:h2:mem:budget-service

### Azure
pam-budget-service uses Postgres as it's RDBMS

To view the database it's recommended to use pgAdmin:
1. Install and start up the program
2. Username and password exists as secrets in Azure

## Environmental variables
Important environment variables and how to set them up to get everything running.

## Swagger
Swagger includes automated documentation of the restful APIs expressed using json. It displays all the endpoints in a project and generates test-cases for those. To be able to test the endpoints, firstly an authorization is needed through OAuth2. After that all endpoints can be tested out. 
<br> 
The site displays which environment that the project are currently running on under the title "Environment:".
When the application is running swagger can be find with this url `http://localhost:23154/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config`
The configuration file for swagger is in **apiconfiguration/OpenapiConfiguration.java**

## Local Tips
* For component-test set property *ikea.imc.pam.budget.service.docker.standalone* to true for faster tests. Keep in mind
though that you, the user, is responsible for the application lifecycle.
