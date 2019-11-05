# MessagingAPI

[![Travis CI](https://travis-ci.com/iamaldren/MessagingAPI.svg?token=JFGXGDBsRwtsPKw6DTAj&branch=master)](https://travis-ci.com/iamaldren/MessagingAPI.svg?token=JFGXGDBsRwtsPKw6DTAj&branch=master) [![codecov](https://codecov.io/gh/iamaldren/MessagingAPI/branch/master/graph/badge.svg?token=6oJH8e2Y17)](https://codecov.io/gh/iamaldren/MessagingAPI)

## What

A simple RESTful application where users can send messages to each other.

## Tech Stack
- Java 8
- Springboot
- Spring Data
- Mongodb
- Gradle
- Docker
- JUnit 5
- Mockito

## Getting Started

The application can be run either via docker or manually via command prompt.

### Using Docker
#### Prequisites
- Docker

The application is containerized, wherein all related stack are already setup.

Just run the docker-compose file, and the application can be used directly.

1. Go to <PROJECT_SOURCE_DIR>. The docker-compose.yml file should be in this directory.
2. Run the command below:
    ```sh
    docker-compose up -d
    ```
3. An instance of mongodb, and the messaging api should be started.

### Manual Running
#### Prerequisites
- Java 8
- Maria DB
- Gradle

1. Run the scripts in <PROJECT_SOURCE_DIR>/src/main/scripts in your Maria DB instance.
2. Update the application-LOCAL.yml file in <PROJECT_SOURCE_DIR>/src/main/resources folder with correct Maria DB credentials.
3. Build the project by executing the command below.
    ```sh
    gradle clean build
    ```
4. Once the build finished successfully, execute the command below to run the application.
    ```sh
    java -jar /build/libs/Messaging-API-1.0.0.jar --spring.profiles.active=LOCAL
    ```