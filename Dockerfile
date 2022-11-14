FROM openjdk:20-slim-buster
EXPOSE 8080
ADD target/banco-pichincha-0.0.1-SNAPSHOT.jar banco-pichincha.jar
ENTRYPOINT ["java","-jar","/banco-pichincha.jar"]