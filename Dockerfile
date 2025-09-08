FROM openjdk:21-jdk
COPY target/*.jar wedding-invite.jar
ENTRYPOINT ["java","-jar","/wedding-invite.jar"]
EXPOSE 2714