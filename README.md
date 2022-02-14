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
* JDBC URL: jdbc:h2:mem:budget-version

### Azure
pam-budget-service uses Postgres as it's RDBMS

To view the database it's recommended to use pgAdmin:
1. Install and start up the program
2. Username and password exists as secrets in Azure

## Environmental variables
Important environment variables and how to set them up to get everything running.


### application-local.properties file
application-local.properties specifies system local properties needed for application to run.
It should be created in **../Application/src/main/resources/**. Important that you name it 
application-local.properties otherwise Spring won't be able to find it.

#### File data
<details>
    <summary>application-local.properties</summary>

    spring.main.allow-bean-definition-overriding=true
    springdoc.swagger-ui.oauth.client-id=CLIENT_ID
    
    #Networking
    ikea.imc.pam.network.domain=http://localhost:${ikea.imc.pam.network.port}
    ikea.imc.pam.budget.service.url = ${ikea.imc.pam.network.domain}/
    
    #Addons local
    ikea.imc.pam.oauth.microsoft.tenant-id=<Ask a colleague for this id>
    ikea.imc.pam.oauth.microsoft.authorization-url=https://login.microsoftonline.com/${ikea.imc.pam.oauth.microsoft.tenant-id}/oauth2/v2.0/authorize
    ikea.imc.pam.oauth.microsoft.token-url=https://login.microsoftonline.com/${ikea.imc.pam.oauth.microsoft.tenant-id}/oauth2/v2.0/token
    ikea.imc.pam.oauth.client-scope.id=<Ask a colleague for this id>
    
    #OpenAPI
    ikea.imc.pam.openapi.documentation.open-api-docs=${ikea.imc.pam.network.domain}/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/
    ikea.imc.pam.openapi.documentation.open-api-json-doc=${ikea.imc.pam.network.domain}/v3/api-docs
    
    #ClientScope
    ikea.imc.pam.oauth.client-scope.read-scope-desc=Read
    ikea.imc.pam.oauth.client-scope.write-scope-desc=Write
    
    #SQL overwrite usr/pass in profile-specific file
    ikea.imc.pam.budget.service.db.port=5432
    spring.datasource.url=jdbc:postgresql://budget-service-postgres:${ikea.imc.pam.budget.service.db.port}/${ikea.imc.pam.budget.service.db.name}
    spring.datasource.username=user
    spring.datasource.password=pass
</details>

## Swagger
Swagger includes automated documentation of the restful APIs expressed using json. It displays all the endpoints in a project and generates test-cases for those. To be able to test the endpoints, firstly an authorization is needed through OAuth2. After that all endpoints can be tested out. 
<br> 
The site displays which environment that the project are currently running on under the title "Environment:".
When the application is running swagger can be find with this url `http://localhost:23154/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config`
The configuration file for swagger is in **apiconfiguration/OpenapiConfiguration.java**
