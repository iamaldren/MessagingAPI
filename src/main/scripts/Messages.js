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

db.users.insertMany([
    {
	    "sender" : "5dac2bef6665503f48e3b3d8",
	    "receiver" : "5dac2bef6665503f48e3b3d9",
	    "subject" : "First",
	    "content" : "First ever message!",
	    "sentDate" : new Date("2019-10-01T22:33:16.030+08:00"),
	    "status" : "READ",
	    "_class" : "com.aldren.messaging.document.Messages"
    },
    {
	    "sender" : "5dac2bef6665503f48e3b3d8",
	    "receiver" : "5dac2bef6665503f48e3b3d9",
	    "subject" : "Second",
	    "content" : "Second message!",
	    "sentDate" : new Date("2019-10-02T12:33:16.030+08:00"),
	    "status" : "READ",
	    "_class" : "com.aldren.messaging.document.Messages"
    },
    {
	    "sender" : "5dac2bef6665503f48e3b3d8",
	    "receiver" : "5dac2bef6665503f48e3b3d9",
	    "subject" : "Third",
	    "content" : "Third message!",
	    "sentDate" : new Date("2019-10-02T22:33:16.030+08:00"),
	    "status" : "READ",
	    "_class" : "com.aldren.messaging.document.Messages"
    },
    {
	    "sender" : "5dac2bef6665503f48e3b3d8",
	    "receiver" : "5dac2bef6665503f48e3b3d9",
	    "subject" : "Skywalkin'",
	    "content" : "By Miguel",
	    "sentDate" : new Date("2019-10-03T08:33:16.030+08:00"),
	    "status" : "READ",
	    "_class" : "com.aldren.messaging.document.Messages"
    },
    {
	    "sender" : "5dac2bef6665503f48e3b3d8",
	    "receiver" : "5dac2bef6665503f48e3b3d9",
	    "subject" : "Sure Thing",
	    "content" : "By Miguel",
	    "sentDate" : new Date("2019-10-03T12:33:16.030+08:00"),
	    "status" : "READ",
	    "_class" : "com.aldren.messaging.document.Messages"
    },
    {
	    "sender" : "5dac2bef6665503f48e3b3d8",
	    "receiver" : "5dac2bef6665503f48e3b3d9",
	    "subject" : "Paranoid",
	    "content" : "Ty Dolla $ign",
	    "sentDate" : new Date("2019-10-03T22:33:16.030+08:00"),
	    "status" : "READ",
	    "_class" : "com.aldren.messaging.document.Messages"
    }
])