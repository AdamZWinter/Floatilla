FROM openjdk:19
COPY ./src/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","HelloWorld"]