FROM openjdk:11
WORKDIR /usr/src
RUN apt-get -y update
RUN apt install wget
RUN apt install unzip
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt -y install ./google-chrome-stable_current_amd64.deb
RUN wget -O /tmp/chromedriver.zip http://chromedriver.storage.googleapis.com/` curl -sS chromedriver.storage.googleapis.com/LATEST_RELEASE`/chromedriver_linux64.zip
RUN mkdir chrome
RUN unzip /tmp/chromedriver.zip chromedriver -d /usr/src/chrome
WORKDIR /
ARG JAR_FILE=build/libs/module-admin.jar
ARG ARG_PROFILE
COPY ${JAR_FILE} ./module-admin.jar
ENV TZ=Asia/Seoul
ENV SPRING_PROFILE=${ARG_PROFILE}
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE}", "-jar", "./module-admin.jar"]
EXPOSE 8085