FROM gradle:latest AS BUILD
COPY . .
RUN gradle vaadinBuildFrontend build

FROM openjdk:latest
COPY --from=BUILD /emma/build/libs/emma.jar emma.jar
EXPOSE 8080
CMD ["java", "-jar", "emma.jar"]
