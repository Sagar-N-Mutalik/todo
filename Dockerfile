# Stage 1: Build the JAR file
FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Download dependencies first (cached layer)
RUN ./mvnw dependency:go-offline
COPY src ./src
# Build and skip tests (tests are done in Jenkins)
RUN ./mvnw clean package -DskipTests

# Stage 2: Extract layers (Standard Spring Boot 3+ optimization)
FROM eclipse-temurin:21-jre-alpine as layers
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 3: The Final Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy layers separately for better caching
COPY --from=layers /app/dependencies/ ./
COPY --from=layers /app/spring-boot-loader/ ./
COPY --from=layers /app/snapshot-dependencies/ ./
COPY --from=layers /app/application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]