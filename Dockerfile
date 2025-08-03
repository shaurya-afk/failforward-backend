# Multi-stage build
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Set environment variables for database connection
ENV SPRING_PROFILES_ACTIVE=""
ENV SPRING_DATASOURCE_URL="jdbc:postgresql://ep-quiet-dawn-a180e803-pooler.ap-southeast-1.aws.neon.tech/failforward_db?sslmode=require&channelBinding=require"
ENV SPRING_DATASOURCE_USERNAME="neondb_owner"
ENV SPRING_DATASOURCE_PASSWORD="npg_Rormv6Ji7DAN"

ENTRYPOINT ["java", "-jar", "app.jar"]