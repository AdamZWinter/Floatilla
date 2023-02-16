FROM openjdk:19
COPY ./out/artifacts/Floatilla_jar/Floatilla.jar /tmp
COPY ./config.json /tmp
WORKDIR /tmp
CMD ["java", "-jar", "Floatilla.jar"]
