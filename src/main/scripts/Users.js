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

db.users.insertMany([
    {
        "userId": "tonystark",
        "firstName": "Tony",
        "lastName": "Stark",
        "status": "ACTIVE",
        "role": "User",
        "updatedDate": new Date("<YYYY-mm-ddTHH:MM:ssZ>")
    },
    {
        "userId": "steverogers",
        "firstName": "Steve",
        "lastName": "Rogers",
        "status": "ACTIVE",
        "role": "User",
        "updatedDate": new Date("<YYYY-mm-ddTHH:MM:ssZ>")
    },
    {
        "userId": "thorodinson",
        "firstName": "Thor",
        "lastName": "Odinson",
        "status": "ACTIVE",
        "role": "User",
        "updatedDate": new Date("<YYYY-mm-ddTHH:MM:ssZ>")
    },
    {
        "userId": "nickfury",
        "firstName": "Nick",
        "lastName": "Fury",
        "status": "ACTIVE",
        "role": "Admin",
        "updatedDate": new Date("<YYYY-mm-ddTHH:MM:ssZ>")
    },
    {
        "userId": "mariahill",
        "firstName": "Maria",
        "lastName": "Hill",
        "status": "ACTIVE",
        "role": "Admin",
        "updatedDate": new Date("<YYYY-mm-ddTHH:MM:ssZ>")
    }
])