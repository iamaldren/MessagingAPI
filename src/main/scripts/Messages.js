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

db.messages.insert(
    {
        "id":null,
        "sender":{"timestamp":1571564527,"machineIdentifier":6710608,"processIdentifier":16200,"counter":14922712,"time":1571564527000,"date":1571564527000,"timeSecond":1571564527},
        "receiver":{"timestamp":1571564527,"machineIdentifier":6710608,"processIdentifier":16200,"counter":14922713,"time":1571564527000,"date":1571564527000,"timeSecond":1571564527},
        "subject":"Hello",
        "content":"World",
        "sentDate":"2019-10-22T22:52:21.072+0800",
        "status":"UNREAD"
    }
)