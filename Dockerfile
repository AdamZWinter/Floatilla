FROM openjdk:19
COPY ./out/artifacts/Floatilla_jar/Floatilla.jar /tmp
COPY ./composeConfig.json /tmp/config.json
COPY ./entrypoint.sh /tmp
WORKDIR /tmp
ENTRYPOINT ["/bin/sh", "entrypoint.sh"]
#CMD ["java", "-jar", "Floatilla.jar"]
