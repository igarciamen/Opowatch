# ---- Build stage: compiles the Spring Boot jar with Maven + JDK 21 ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the POM first and pre-download dependencies (better layer caching).
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copy the sources and build the jar (tests are run separately, skipped here).
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Runtime stage: small image with only the JRE and the jar ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# The Spring Boot repackaged jar is the only *.jar in target (not *.jar.original).
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
