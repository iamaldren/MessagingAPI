FROM java:8

VOLUME /tmp

EXPOSE 8080

RUN mkdir /app

ADD /build/libs/MessagingAPI-1.0.0.jar /app/MessagingAPI-1.0.0.jar

COPY . /home/MessagingAPI

RUN curl -L https://services.gradle.org/distributions/gradle-5.6.3-bin.zip -o gradle-5.6.3-bin.zip
RUN apt-get install -y unzip
RUN unzip gradle-5.6.3-bin.zip -d /app

ENV GRADLE_HOME=/app/gradle-5.6.3
ENV PATH=$PATH:$GRADLE_HOME/bin

ENTRYPOINT ["java", "-jar", "/app/MessagingAPI-1.0.0.jar"]