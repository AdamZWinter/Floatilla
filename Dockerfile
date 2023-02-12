FROM openjdk:19
COPY ./out/artifacts/Floatilla_jar/Floatilla.jar /tmp
WORKDIR /tmp
CMD ["java", "-jar", "Floatilla.jar"]
