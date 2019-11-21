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