# Job (DAG) Management Application

## Overview

This Spring Boot application simulates the creation and execution of Directed Acyclic Graphs (DAGs) on Apache Airflow. It generates DAG Python scripts and mocks Airflow's API, providing a testing and development environment for Airflow-based workflows.

## Features

- Create and manage DAG configurations
- Generate Python scripts for DAGs
- Mock Airflow API for DAG execution
- Secure API access with Spring Security
- Interactive API documentation with Swagger

## Technology Stack

- Java
- Spring Boot
- Spring Security
- Maven
- Swagger

## Getting Started

### Prerequisites

- Java JDK 17 or higher
- Maven 3.6 or higher
- Git

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/joyce-adelle/job-manager.git
   ```

2. Navigate to the project directory:
   ```
   cd job-manager
   ```

3. Install dependencies:
   ```
   mvn clean install
   ```

### Running the Application

1. Start the application using Maven:
   ```
   mvn spring-boot:run
   ```

2. The application will start on `http://localhost:8081` by default.

## Usage

### API Documentation

Access the Swagger UI for interactive API documentation:

```
http://localhost:8081/swagger-ui/index.html
```

### Authentication

The API uses Basic Authentication. Use the following test user credentials:

- Username: `firstUser`, Password: `first1password`
- Username: `secondUser`, Password: `second2password`

### Creating and Running DAGs

1. Use the API endpoints documented in Swagger to create new DAGs.
2. To run a DAG, use the endpoint:
   ```
   POST http://localhost:8081/api/jobs/{id}/run
   ```
   Replace `{id}` with the ID of the DAG you want to run.

### Test DAG Ids

The following pre-configured DAG Ids are available for testing:

- `sample_dag_1`
- `sample_dag_2`
- `sample_dag_3`

## Configuration

- The default profile is `dev`, which mocks the Airflow API. Change this profile to use an actual Airflow connection.
- DAG Python scripts are saved in `./airflow/dag` by default. This location can be configured in `application.yml`.

## Development

### Building the Project

```
mvn clean package
```

### Running Tests

```
mvn test
```

## Troubleshooting

If you encounter any issues, please check the following:

1. Ensure all prerequisites are installed and properly configured.
2. Verify that the application is running on the correct port (8081 by default).
3. Check the application logs for any error messages.
