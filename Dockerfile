FROM gradle:latest AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle vaadinBuildFrontend build

FROM openjdk:latest
COPY --from=BUILD /usr/app/build/libs/emma.jar emma.jar
EXPOSE 8080
CMD ["java", "-jar", "emma.jar"]
