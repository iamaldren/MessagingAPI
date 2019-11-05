FROM java:8

VOLUME /tmp

EXPOSE 8080

ADD /build/libs/MessagingAPI-1.0.0.jar MessagingAPI-1.0.0.jar

ENTRYPOINT ["java", "-jar", "MessagingAPI-1.0.0.jar"]