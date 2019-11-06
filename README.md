# MessagingAPI

[![Build Status](https://travis-ci.org/iamaldren/MessagingAPI.svg?branch=master)](https://travis-ci.org/iamaldren/MessagingAPI) [![codecov](https://codecov.io/gh/iamaldren/MessagingAPI/branch/master/graph/badge.svg?token=6oJH8e2Y17)](https://codecov.io/gh/iamaldren/MessagingAPI)

A simple RESTful application where users can send messages to each other.

## Getting Started

1. Clone the repository from Github
2. Run the application. There are 2 ways to run this, first is via Docker, and the second one is manually building and executing the jar file.

### Docker
#### Prequisite/s
- Docker

The application is containerized, wherein all related stacks are already set up in the docker images. One just needs to run the docker-compose file, and the application can be used directly.

1. Go to `PROJECT_SOURCE_DIR`. You should see the docker-compose.yml file in this directory.
    ```
    app
    |-- src                     #Source codes
    |-- gradle                  #Gradle Wrapper
    |-- .gitignore
    |-- travis.yml              #Travis setup
    |-- build.gradle            #Gradle configurations
    |-- codecov.yml
    |-- docker-compose.yml      #Docker setup for running the Mongodb and Message-API images
    |-- Dockerfile
    |-- gradlew
    |-- gradlew.bat
    |-- lombok.config
    |-- README.md
    |-- settings.gradle
    ```
2. Run the command below via command prompt.
    ```sh
    docker-compose up -d
    ```
3. An instance of Mongodb and Messaging API should have been started.
4. Run the command below to check all active containers.
    ```
    docker ps
    ```
5. The application can be accessed in port 8080.

### Manual 
#### Prerequisite/s
- Java 8
- Mongodb
- Gradle

1. Run the scripts in the folder structure below in your Mongodb instance.
```
app
|--src
|----main
|------scripts      #Mongodb scripts
```
2. Update the application-LOCAL.yml file, located in the folders structure below, with correct Mongodb credentials and URL.
```
app
|-- src
|----main
|------resources    #Contains the application-LOCAL.yml file
```
3. Build the project by executing the command below.
    ```sh
    gradle clean build
    ```
4. After finishing the build, execute the command below to run the application.
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

By default, the application has 5 default users whose user IDs are:
- tonystark
- steverogers
- nickfury
- mariahill
- thorodinson

These IDs can be used for the purpose of testing the application.

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

X-User is the current system user.
`WARNING`: In a real world scenario, the `sender` shouldn't be passed in the X-User header. Proper authentication should be implemented.

The endpoint will return an `HTTP Status 404` in case the receiver does not exists in the database. `Sender` user will not be validated, it is assumed that the user is existing in the database since he can use the system.

Once the message has been sent to the user, it will have a message status of `UNREAD`.

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

This endpoint will only display all `UNREAD` messages for the user, if there are none it will return an empty list.

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

The `page` parameter must always be numeric, and has a minimum value of 1. If the value passed to the parameter is less than 1, it will throw an `HTTP Status 400`.

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

The list of all received messages can be retrieved in this endpoint. 

It has the same behavior as the `/api/v1/message/sent` endpoint.

### Prediction of number of messages received

The endpoint will have a `type` parameter, which will only accept 2 string values. The values supported are `Day` and `Week`, both are `case insensitive`.

In case any other values, aside from the mentioned ones, were passed to the parameter, the endpoint will throw an `Http Status 400`.

#### Extra Endpoint

There is an extra endpoint that can be invoked to generate dummy data whose date will be within the last 30 days from current date.

This is for the purpose of having a data that can be used for predicting the number of messages per day/week.

The generated messages will have a random sender/receiver based on the 5 default users.

The endpoint can be access thru `/api/v1/test`

#### For the Day
```sh
GET /api/v1/message/predict?type=Day

Response Body:
{
    "timestamp": "2019-11-06T11:27:13.547+0000",
    "status": 200,
    "description": "OK",
    "information": "Predicted message count to receive for the day is 22"
}
```
The endpoint will get the total number of messages sent within the last 14 days from current date. The total count will then be divided by 14 to get the average, which will be the predicted count of the number of messages going to be received for the day.

#### For the Week
```sh
GET /api/v1/message/predict?type=Week

Response Body:
{
    "timestamp": "2019-11-06T11:35:26.901+0000",
    "status": 200,
    "description": "OK",
    "information": "Predicted message count to receive for the week is 116"
}
```
The endpoint will get the total number of messages sent within the last 30 days from current date. The total count will then be divided by 4 (number of weeks within a month) to get the average, which will be the predicted count of the number of messages going to be received for the week.

## Unit and Integration tests

The unit and integration tests will run once the project is building, or when the command below is executed.
```sh
gradle test
```

The tests are located in the folder structure shown below.
```
app
|-- src
|---- test
|------ java
|-------- com
|---------- aldren
|------------ messaging
|-------------- AppTest.java     #Integration tests
|-------------- unit             #Contain Unit tests classes
```

### Integration Tests

The integration tests are using an embedded Mongodb for database transactions. Mocking of data is being avoided so the end-to-end code process will be executed.

`WARNING`: Though the purpose of an integration test is to check the end-to-end process, including database transactions, it is still advisable to do testing with an actual database instance. An embedded database may behave differently compared to the actual one.

### Unit Tests

The unit tests are doing data mock-ups for all database related transactions. Its purpose is mainly to test the logic flow per each function/method of the classes.

## Mongodb collections

There are 2 collections in Mongodb for this application.
- Users -> Collection to store all users of the application. 
    ```
    db.createCollection("users", {
        validator: {
            $jsonSchema: {
                bsonType: "object",
                required: [ "userId", "firstName", "lastName", "status", "role" ],
                properties: {
                    userId: {
                        bsonType: "string"
                    },
                    firstName: {
                        bsonType: "string"
                    },
                    lastName: {
                        bsonType: "string"
                    },
                    status: {
                        enum: [ "ACTIVE", "INACTIVE" ]
                    },
                    role: {
                        enum: [ "User", "Admin" ]
                    },
                    updatedDate: {
                        bsonType: "date"
                    }
                }
            }
        }
    })
    ```
- Messages -> Collection to store all messages. This has a relationship to Users collection through the `generated ID` by Mongodb for the users data in the collection.
    ```
     db.createCollection("messages", {
         validator: {
             $jsonSchema: {
                 bsonType: "object",
                 required: [ "sender", "receiver", "subject", "content", "sentDate" ],
                 properties: {
                     sender: {
                         bsonType: "string"
                     },
                     receiver: {
                         bsonType: "string"
                     },
                     subject: {
                         bsonType: "string"
                     },
                     content: {
                         bsonType: "string"
                     },
                     sentDate: {
                         bsonType: "date"
                     },
                     status: {
                         enum: [ "READ", "UNREAD" ]
                     }
                 }
             }
         }
     })   
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
