# pam-budget-service

To be able to run the application properly you need to have all this installed

- An IDE (Intellij recommended)
- Java 11
- Docker

Download the project to your computer and follow the steps below on
how to set everything up.

## How to run & debug application in local environment
### Add project to Intellij (Recommended)

1. git clone git@github.com:icomdev/pam-budget-service.git
2. IntelliJ -> New -> Project from Existing Sources...
3. Select the root pom
4. After the project is loaded, edit 'StartSpringbootApi' configuration, add environment variables.
5. Run the application


Navigate to the root of pam-budget-service **mvn clean install**, this will create everything that pam-budget-service
need itself.

To debug connect a *Remote JVM Debug* configuration on port **5006**

### Environmental variables

Important environment variables and how to set them up to get everything running.
<br> *Note: The variables are set in the StartSpringbootApi configuration in Intellij as it holds configuration settings for the environment you will be using*

Budget-service uses the following environment variables:
* spring.profiles.active=local

## Ports

| Service            | Port |
|--------------------|----|
| pam-budget-service | 23154 |
| Remote JVM Debug   | 5006 |


## Database

### Local

pam-budget-service uses the H2 engine to run an embedded Postgres db in local and its pipelines.

To view the database:

1. Start pam-budget-service application
2. Go to localhost:23154/h2-console
3. User name: sa
4. JDBC URL: jdbc:h2:mem:budget-service

#### H2 Database Credentials

Note: Database credentials in application-local.properties must be correct
* spring.datasource.username=sa
* spring.datasource.password=

#### Azure Database

pam-budget-service uses Postgres as it's RDBMS

To view the database it's recommended to use pgAdmin:

1. Install and start up the program
2. Username and password exists as secrets in Azure

## Swagger

Swagger includes automated documentation of the restful APIs expressed using json. It displays all the endpoints in a project and generates test-cases
for those. To be able to test the endpoints, firstly an authorization is needed through OAuth2. After that all endpoints can be tested out.
<br>
The site displays which environment that the project are currently running on under the title "Environment:".
<br>
When the budget-Service application is running:
* To access the site for the Local environment, swagger can be found with this url `http://localhost:23154/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/`
* To access the site for the Dev environment, swagger can be found with this url `https://app-pam-budget-service-dev.azurewebsites.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/`
* To access the site for the Test environment, swagger can be found with this url `https://app-pam-budget-service-test.azurewebsites.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/`
* To access the site for the stage environment, swagger can be found with this url `https://app-pam-budget-service-stage.azurewebsites.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/`
* To access the site for the Prod environment, swagger can be found with this url `https://app-pam-budget-service-prod.azurewebsites.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/`

The configuration file for swagger is in **apiconfiguration/OpenapiConfiguration.java**

Note: All environment except local are protected using client ID. To get access to the environments, fetch client ID from Azure Key Vault.

## Client URL for Environment

* Local: http://localhost:3000
* Stage: https://stage.forena.inter.ikea.net/
* Prod: https://forena.inter.ikea.net/

## Testing (Local Tips)

* For component-test, set property *ikea.imc.pam.budget.service.docker.standalone* to true for faster tests
* Run the application before running the component test. Make sure that your docker is running
* Keep in mind though that you, the user, is responsible for the application lifecycle.

## How to deploy

1. Create a branch from main
2. Make changes in code and commit
3. Create a pull request to master and wait for approval
4. Merge to main (Deploys automatically to dev)
5. Test in dev environment
6. If test is successful, click on deploy in dev environment. This will deploy to test
7. Test in test environment
8. If test is successful, click on deploy in test environment. This will deploy to stage
9. Test in stage environment
10. If test is successful, click on deploy in stage environment. This will deploy to prod
11. Test in prod environment if needed

## Docker

Docker is used to run the application in a container. The dockerfile is located in the root of the project.

## How to use BudgetClient

The BudgetClient has to be configured to work properly with the following values:

```
ikea.imc.pam.budget.service.url = <URL of the budget service>
ikea.imc.pam.budget.service.registration.id=pam-budget-service <if changed, the azure...-clients.pam-budget-service... has to be changed to the same>

azure.activedirectory.authorization-clients.pam-budget-service.authorization-grant-type=on_behalf_of
azure.activedirectory.authorization-clients.pam-budget-service.scopes[0]= <The same value as in the budgets azure.activedirectory.app-id-uri config>

spring.security.oauth2.client.registration.pam-budget-service.authorization-grant-type=client_credentials
spring.security.oauth2.client.registration.pam-budget-service.client-id=<The same value as in the budgets azure.activedirectory.client-id config>
spring.security.oauth2.client.registration.pam-budget-service.client-secret=<The same value as in the budgets azure.activedirectory.app-secret config>
spring.security.oauth2.client.provider.pam-budget-service.token-uri=<The same value as in the budgets com.ikea.imc.pam.oauth.microsoft.token-url config>
```

Example:

```
ikea.imc.pam.budget.service.url = https://ikea.budgetservice.com/
ikea.imc.pam.budget.service.registration.id=change-value

azure.activedirectory.authorization-clients.change-value.authorization-grant-type=on_behalf_of
azure.activedirectory.authorization-clients.change-value.scopes[0]= api://0000aaaa-0000-aaaa-0000-000000000000/budget-ad

spring.security.oauth2.client.registration.change-value.authorization-grant-type=client_credentials
spring.security.oauth2.client.registration.change-value.client-id=0000aaaa-0000-aaaa-0000-000000000000
spring.security.oauth2.client.registration.change-value.client-secret=00aa00aa00aa@@00!!00--00aa00aa00aa00a
spring.security.oauth2.client.provider.change-value.token-uri=https://login.microsoftonline.com/tenantid-0000-0000-0000-aa00/oauth2/v2.0/token
```
Changes in Budget-Service-Client means the version of the client has to be changed in the pom.xml of the project that uses the client (Version can be found in Azure Artifacts).
<br>

