FROM amazoncorretto:17
ADD target/microservice-frontend-0.0.1-SNAPSHOT.jar app.jar
ENV DB_URL jdbc:mysql://host.docker.internal/petclinic
ENTRYPOINT [ "sh", "-c", "java -jar /app.jar" ]