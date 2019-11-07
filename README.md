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

### Requirement

It will be enough if the message contains the following fields:
- sender
- receiver
- subject
- content
- sent date

It should be a restful application, no ui needed. The required endpoints are: 
- Send message
- List all received messages for a user
- List all sent messages by a user
- Read message details
- An endpoint to estimate the number of total messages that will be sent probably for the rest of the day
- An endpoint to estimate the number of total messages that will be sent probably for the rest of the week

### Endpoints

By default, the application has 5 default users whose user IDs are:
- tonystark
- steverogers
- nickfury
- mariahill
- thorodinson

These IDs can be used for the purpose of testing the application.

#### Send Message
```sh
POST /api/v1/messages

Request Body:
{
    "sender": "tonystark",
    "receiver": "steverogers",
    "subject": "Shawarma",
    "content": "Where is that shawarma place again?"
}
```

The endpoint will return an `HTTP Status 404` in case the receiver does not exists in the database. `Sender` user will not be validated, it is assumed that the user is existing in the database since he can use the system.

`Curl Command`:
```
curl -d '{"sender":"tonystark", "receiver":"steverogers", "subject": "Shawarma", "content": "Where is that shawarma place again?"}' -H "Content-Type: application/json" -X POST http://localhost:8080/api/v1/messages
```
#### Read Message Detail
```sh
GET /api/v1/messages/{id}

Response Body:
{
    "id": "5dc39284fca1360001ec8896",
    "sender": "Tony Stark",
    "receiver": "Steve Rogers",
    "subject": "Shawarma",
    "content": "Where is that shawarma place again?",
    "sentDate": "2019-11-07T03:41:56.486+0000"
}
```

This endpoint will display the details for a specific message. The message will be identified by the message ID passed as a Path variable in the URL.

In case the message ID passed doesn't exists in the database, it will throw an `HTTP Status 404`.

`Curl Command`:
```
curl http://localhost:8080/api/v1/messages/{id}
```

#### List of all Sent Messages by a User
```sh
GET /api/v1/messages?sender=tonystark&page=1

Response Body:
{
    "totalPage": 10,
    "messages": [
        {
            "id": "5dc39284fca1360001ec8896",
            "receiver": "Steve Rogers",
            "subject": "Shawarma",
            "sentDate": "2019-11-07T03:41:56.486+0000"
        },
        {
            "id": "5dc3925afca1360001ec887c",
            "receiver": "Maria Hill",
            "subject": "Test 4",
            "sentDate": "2019-11-06T03:41:14.874+0000"
        },
        {
            "id": "5dc3925afca1360001ec887e",
            "receiver": "Maria Hill",
            "subject": "Test 6",
            "sentDate": "2019-11-06T03:41:14.874+0000"
        },
        {
            "id": "5dc3925afca1360001ec8883",
            "receiver": "Maria Hill",
            "subject": "Test 11",
            "sentDate": "2019-11-06T03:41:14.874+0000"
        }
    ]
}
```

The list of all sent messages can be retrieved through this endpoint, and it implements pagination. The response will return a JSON object that indicates the number of total pages, and the list of messages in that page. The maximum number of messages per page is `10`.

The `page` parameter must always be numeric, and has a minimum value of 1. If the value passed to the parameter is less than 1, it will throw an `HTTP Status 400`.

The `sender` parameter is required for retrieving the list of all sent messages.

`Curl Command`:
```sh
curl http://localhost:8080/api/v1/messages?sender=tonystark&page=1
```

#### List of all Received messages for a User
```sh
GET /api/v1/messages?receiver=steverogers&page=1

Response Body:
{
    "totalPage": 10,
    "messages": [
        {
            "id": "5dc39284fca1360001ec8896",
            "sender": "Tony Stark",
            "subject": "Shawarma",
            "sentDate": "2019-11-07T03:41:56.486+0000"
        },
        {
            "id": "5dc3925afca1360001ec8879",
            "sender": "Maria Hill",
            "subject": "Test 1",
            "sentDate": "2019-11-06T03:41:14.874+0000"
        },
        {
            "id": "5dc3925afca1360001ec887d",
            "sender": "Thor Odinson",
            "subject": "Test 5",
            "sentDate": "2019-11-06T03:41:14.874+0000"
        },
        {
            "id": "5dc3925afca1360001ec887f",
            "sender": "Maria Hill",
            "subject": "Test 7",
            "sentDate": "2019-11-06T03:41:14.874+0000"
        }
    ]
}
```

The list of all received messages can be retrieved in this endpoint. 

The `page` parameter must always be numeric, and has a minimum value of 1. If the value passed to the parameter is less than 1, it will throw an `HTTP Status 400`.

The `receiver` parameter is required for retrieving the list of all received messages.

`Curl Command`:
```sh
curl http://localhost:8080/api/v1/messages?receiver=steverogers&page=1
```

#### Prediction of number of messages received

The endpoint will have a `forecast` parameter, which will only accept 2 string values. The values supported are `Day` and `Week`, both are `case insensitive`.

In case any other values, aside from the mentioned ones, were passed to the parameter, the endpoint will throw an `Http Status 400`.

##### Extra Endpoint

There is an extra endpoint that can be invoked to generate dummy data whose date will be within the last 30 days from current date.

This is for the purpose of having a data that can be used for predicting the number of messages per day/week.

The generated messages will have a random sender/receiver based on the 5 default users.

The endpoint can be access thru `/api/v1/test`

`Curl Command`:
```sh
curl http://localhost:8080/api/v1/test
```

##### For the Day
```sh
GET /api/v1/messages?forecast=Day

Response Body:
{
    "timestamp": "2019-11-06T11:27:13.547+0000",
    "status": 200,
    "description": "OK",
    "information": "Predicted message count to receive for the day is 22"
}
```
The endpoint will get the total number of messages sent within the last 14 days from current date. The total count will then be divided by 14 to get the average, which will be the predicted count of the number of messages going to be received for the day.

`Curl Command`:
```sh
curl http://localhost:8080/api/v1/messages?forecast=Day
```

##### For the Week
```sh
GET /api/v1/messages?forecast=Week

Response Body:
{
    "timestamp": "2019-11-06T11:35:26.901+0000",
    "status": 200,
    "description": "OK",
    "information": "Predicted message count to receive for the week is 116"
}
```
The endpoint will get the total number of messages sent within the last 30 days from current date. The total count will then be divided by 4 (number of weeks within a month) to get the average, which will be the predicted count of the number of messages going to be received for the week.

`Curl Command`:
```sh
curl http://localhost:8080/api/v1/messages?forecast=Week
```

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
