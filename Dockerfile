FROM gradle:7.5.1-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17-jdk-slim

RUN mkdir /app

COPY --from=build /home/gradle/src/build/distributions/CountOnMe2-1.0.tar /app
RUN tar -xf /app/CountOnMe2-1.0.tar && rm /app/CountOnMe2-1.0.tar

ENTRYPOINT ["./app/CountOnMe2-1.0/bin/CountOnMe2"]