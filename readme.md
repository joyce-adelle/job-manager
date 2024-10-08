## Job (DAG) Management Application, Spring Boot

Application that simulates creating and running DAGs on Apache Airflow

## Documentation:

Can be viewed with Swagger via url http://localhost:8081/swagger-ui/index.html

## Built With

- Java
- Spring Boot
- Spring Security
- Maven
- Swagger

## Test users

username: `firstUser`  password: `first1password`  
username: `secondUser`  password: `second2password`

## Test DAGs

Can be used to test endpoints for running DAGs http://localhost:8081/api/jobs/{id}/run

id: `sample_dag_1`
id: `sample_dag_2`
id: `sample_dag_3`

Any created DAG can also be ran with it's returned id

## How to run and test the code

- Clone repository
- Install dependencies
- Run the Main application in choice IDE
- View documentation at swagger URL above
- API uses basic authentication, use test users credentials above
- Follow defined schema to test each endpoint
- To run DAG, create and use returned Id or test DAGs above 