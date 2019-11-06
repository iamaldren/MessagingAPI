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

## Mongodb collections

There are 2 collections in Mongodb for this application.
- Users -> Collection to store all users of the application. 
- Messages -> Collection to store all messages. Mapped to users collection by the Users collection generated id.

Please see the scripts in <PROJECT_SOURCE_DIR>/src/main/scripts.

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

X-User is the current user using the system.

The endpoint will return an HTTP Status 404 in case the receiver is not existing in the database. "Sender" user will not be validated as it is assumed that since the user can use the system, the user is existing in the database.

Once the message is sent to the users, it will have a status of UNREAD.

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

The messages will be updated to READ status once they have been called from this endpoint. In case there were an issue with updating, the endpoint will throw an HTTP Status 500.

### List of all Sent Messages
```sh
GET /api/v1/message/sent?page=1
X-User: tonystark

Response Body:
{
    "totalPage": 10,
    "messages": [
        {
            "sender": "tonystark",
            "receiver": "mariahill",
            "subject": "Test 4",
            "content": "Test message 4 from 1 day/s ago",
            "sentDate": "2019-10-11T23:49:26.652+0000"
        },
        {
            "sender": "tonystark",
            "receiver": "thorodinson",
            "subject": "Test 3",
            "content": "Test message 3 from 2 day/s ago",
            "sentDate": "2019-10-09T23:49:26.644+0000"
        },
        {
            "sender": "tonystark",
            "receiver": "steverogers",
            "subject": "Test 1",
            "content": "Test message 1 from 3 day/s ago",
            "sentDate": "2019-10-07T23:49:26.615+0000"
        }
    ]
}
```

The list of all sent messages can be retrieve through paging. The response will return a JSON object that indicates the number of total pages, and the list of messages for that page. The maximum number of messages per page is 10.

The 'page' parameter must always be numeric, and minimum value of 1. Value less than 1 will throw an HTTP Status 400.

### List of all Received messages
```sh
GET /api/v1/message/receive?page=1
X-User: steverogers

Response Body:
{
    "totalPage": 10,
    "messages": [
        {
            "sender": "nickfury",
            "receiver": "steverogers",
            "subject": "Test 2",
            "content": "Test message 2 from 1 day/s ago",
            "sentDate": "2019-11-04T23:49:26.910+0000"
        },
        {
            "sender": "nickfury",
            "receiver": "steverogers",
            "subject": "Test 7",
            "content": "Test message 7 from 1 day/s ago",
            "sentDate": "2019-11-04T23:49:26.910+0000"
        },
        {
            "sender": "thorodinson",
            "receiver": "steverogers",
            "subject": "Test 8",
            "content": "Test message 8 from 1 day/s ago",
            "sentDate": "2019-11-04T23:49:26.910+0000"
        }
    ]
}
```

The list of all received messages can be retrieve through paging. 

Same behavior as the sent messages endpoint.