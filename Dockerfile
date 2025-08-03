FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/deaddocs_backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Set environment variables for database connection
ENV SPRING_PROFILES_ACTIVE=""
ENV SPRING_DATASOURCE_URL="jdbc:postgresql://ep-quiet-dawn-a180e803-pooler.ap-southeast-1.aws.neon.tech/failforward_db?sslmode=require&channelBinding=require"
ENV SPRING_DATASOURCE_USERNAME="neondb_owner"
ENV SPRING_DATASOURCE_PASSWORD="npg_Rormv6Ji7DAN"

ENTRYPOINT ["java", "-jar", "app.jar"]