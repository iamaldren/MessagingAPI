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
4. The application port should be in 8080.

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
## Unit and Integration tests

The unit and integration tests will run once the project is building, or when the command below is executed.
```sh
gradle test
```

### Integration Test

The integration test is under /src/test/java/com/aldren/messaging/AppTest.java.

The tests are using an embedded mongodb for database related actions. Mocking the data is being avoided for the integration testing, so the test will cover end-to-end process.

### Unit Test

The unit test is under /src/test/java/com/aldren/messaging/unit/*

The tests are using Mockito to mock the data needed, and focuses more on the logic behind every function/method.

## Messaging API Application

The application, is a RESTful application that exposes 5 endpoints.
- /api/v1/message/send
- /api/v1/message/read
- /api/v1/message/sent
- /api/v1/message/receive
- /api/v1/message/predict

By default, the application has 5 existing users whose user ids are:
- tonystark
- steverogers
- nickfury
- mariahill
- thorodinson

These users can be used for the purpose of testing the application.

### Send Message
```sh
POST /api/v1/message/send
X-User: tonystark

Request Body:
{
    "receiver": "steverogers",
    "subject": "Shawarma",
    "content": "Where is that shawarma place again?"
}
```

### Read Message
```sh
POST /api/v1/message/read
X-User: steverogers

Response Body:
[
    {
        "sender": "nickfury",
        "receiver": "mariahill",
        "subject": "Avengers Initiative",
        "content": "Let's start the initiative.",
        "sentDate": "2019-11-05T15:53:52.779+0000"
    }
]
```

This endpoint will only display all UNREAD messages, if there are no UNREAD messages left, this endpoint will return an empty list.