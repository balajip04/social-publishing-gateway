
FROM repository.cars.com/openjdk-jdk8-alpine33:0.0.1

ENV DT_AGENT_PATH=
WORKDIR /app
VOLUME /tmp
ADD . /app
RUN chmod +x start.sh &&     /app/gradlew build
EXPOSE 9000
ENTRYPOINT ["./start.sh"]
