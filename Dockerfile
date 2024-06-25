FROM ubuntu:22.04

RUN apt update
RUN apt install wget gnupg unzip -y

RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
RUN sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list'
RUN apt update

RUN apt install google-chrome-stable -y
RUN wget -q https://chromedriver.storage.googleapis.com/$(google-chrome --sandbox --version | grep -oE '[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+')/chromedriver_linux64.zip || wget https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/$(google-chrome --sandbox --version | grep -oE '[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+')/linux64/chromedriver-linux64.zip
RUN unzip -j chromedriver-linux64.zip chromedriver-linux64/chromedriver chromedriver || true

FROM gradle:8.4-alpine AS BUILD
COPY --chown=gradle:gradle . /home/gradle
RUN gradle vaadinBuildFrontend build

FROM openjdk:latest
COPY --from=BUILD /home/gradle/build/libs/gradle.jar gradle.jar
EXPOSE 8080
CMD ["java", "-jar", "gradle.jar"]
