# MessagingAPI

[![Travis CI](https://travis-ci.com/iamaldren/MessagingAPI.svg?token=JFGXGDBsRwtsPKw6DTAj&branch=master)](https://travis-ci.com/iamaldren/MessagingAPI.svg?token=JFGXGDBsRwtsPKw6DTAj&branch=master) [![codecov](https://codecov.io/gh/iamaldren/MessagingAPI/branch/master/graph/badge.svg?token=6oJH8e2Y17)](https://codecov.io/gh/iamaldren/MessagingAPI)

A simple RESTful application where users can send messages to each other.

## Getting Started

1. Clone the repository from Github
2. Run the application. There are 2 ways to run this, first is via Docker, and the second one is manually building and executing the jar file.

### Docker
#### Prequisites
- Docker

The application is containerized, wherein all related stacks are already setup in the docker images. One just need to run the docker-compose file, and the application can be used directly.

1. Go to `PROJECT_SOURCE_DIR`. You should see the docker-compose.yml file in this directory.
2. Run the command below via command prompt.
    ```sh
    docker-compose up -d
    ```
3. An instance of Mongodb, and the Messaging API should be started.
4. The application can be accessed in port 8080.

### Manual 
#### Prerequisites
- Java 8
- Mongodb
- Gradle

1. Run the scripts in the folder structure below in your Mongo DB instance.
```
|--src
|----main
|------scripts      #Mongodb scripts
```
2. Update the application-LOCAL.yml file, located in the folders structure below, with correct Mongodb credentials and URL.
```
|-- src
|----main
|------resources
```
3. Build the project by executing the command below.
    ```sh
    gradle clean build
    ```
4. Once the build finished successfully, execute the command below to run the application.
    ```sh
    java -jar /build/libs/Messaging-API-1.0.0.jar --spring.profiles.active=LOCAL
    ```

## Messaging API Application

The application is a RESTful application that exposes 5 endpoints.
- /api/v1/message/send
- /api/v1/message/read
- /api/v1/message/sent
- /api/v1/message/receive
- /api/v1/message/predict

By default, the application has 5 existing users whose user IDs are:
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

The endpoint will return an `HTTP Status 404` in case the receiver is not existing in the database. `Sender` user will not be validated as it is assumed that the user is existing in the database, since he can use the system.

Once the message is sent to the user, it will have a message status of `UNREAD`.

### Read Message
```sh
GET /api/v1/message/read
X-User: mariahill

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

This endpoint will only display all `UNREAD` messages for the user, if there are no UNREAD messages it will return an empty list.

The messages will be updated to `READ` status after they have been accessed. In case there was an issue with updating, the endpoint will throw an `HTTP Status 500`.

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

The list of all sent messages can be retrieved through this endpoint, and it implements pagination. The response will return a JSON object that indicates the number of total pages, and the list of messages in that page. The maximum number of messages per page is `10`.

The `page` parameter must always be numeric, and has a minimum value of 1. If the value pass to the parameter is less than 1, it will throw an `HTTP Status 400`.

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

The list of all received messages can be retrieve in this endpoint. 

It has the same behavior as the `/api/v1/message/sent` endpoint.

### Prediction of number of messages received

#### For the Day

#### For the Week

## Unit and Integration tests

The unit and integration tests will run once the project is building, or when the command below is executed.
```sh
gradle test
```

The tests are located in the folder structure shown below.
```
|-- src
|----test
|------java
|--------com
|----------aldren
|------------messaging
|--------------AppTest.java     #Integration tests
|--------------unit             #Contain Unit tests classes
```

### Integration Tests

The integration tests are using an embedded Mongodb for database transactions. Mocking of data is being avoided so the end-to-end code process will be executed.

`WARNING`: Though the purpose of an integration test is to check the flow of end-to-end process, including database transactions, it is still advisable to do testing with an actual database instance. Embedded database may behave differently compared to the actual one.

### Unit Tests

The unit tests are doing data mock-ups for all database related transactions. Its purpose is mainly to test the logic flow per each function/method of the classes.

## Mongodb collections

There are 2 collections in Mongodb for this application.
- Users -> Collection to store all users of the application. 
- Messages -> Collection to store all messages. This has a relationship to Users collection through the `generated ID` by Mongodb for the users data in the collection.

Please see the scripts in the structure below.
```
|--src
|----main
|------scripts      #Mongodb scripts
```

## Tech Stack
- Java 8
- Springboot
- Spring Data
- Mongodb
- Gradle
- Docker
- JUnit 5
- Mockito
