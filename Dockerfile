# ===== Derleme Aşaması (Build Stage) =====
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ===== Çalıştırma Aşaması (Run Stage) =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# SQLite veritabanının Render'da sıfırlanmaması için kalıcı disk yolu (/data) tanımlıyoruz
ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/data/limittakip.db

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
