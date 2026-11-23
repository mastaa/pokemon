# syntax=docker/dockerfile:1
# =============================================================================
# Etap 1 warsztatu: obraz Docker aplikacji Pokédex (Angular + Spring Boot)
# Build wieloetapowy (multi-stage):
#   1) frontend  – budujemy Angulara (Node)
#   2) backend   – budujemy fat-jar Spring Boot (JDK + Gradle)
#   3) runtime   – lekki obraz tylko z JRE i gotowym jarem
# =============================================================================

# ---------- 1) FRONTEND ------------------------------------------------------
FROM node:24-alpine AS frontend
WORKDIR /workspace/frontend
# Najpierw tylko package*.json -> warstwa z node_modules cache'uje się,
# dopóki nie zmienią się zależności.
COPY frontend/package.json frontend/package-lock.json ./
# Uwaga: package-lock.json w repo nie jest zsynchronizowany z package.json,
# dlatego "npm install" zamiast "npm ci" (po naprawie locka wróć do npm ci).
RUN npm install --no-audit --no-fund
COPY frontend/ ./
# angular.json ma outputPath = ../src/main/resources/static
RUN npx ng build

# ---------- 2) BACKEND -------------------------------------------------------
FROM eclipse-temurin:25-jdk AS backend
WORKDIR /workspace
# Wrapper i skrypty Gradle -> osobna warstwa (cache pobranej dystrybucji)
COPY gradlew settings.gradle build.gradle ./
COPY gradle/ gradle/
RUN ./gradlew --no-daemon dependencies > /dev/null
COPY src/ src/
# Podmieniamy statyczne pliki na świeżo zbudowany frontend
COPY --from=frontend /workspace/src/main/resources/static/ src/main/resources/static/
# Frontend zbudowaliśmy w etapie 1, więc pomijamy zadania npm w Gradle
RUN ./gradlew --no-daemon bootJar -x npmInstall -x buildFrontend \
 && cp build/libs/*-SNAPSHOT.jar app.jar

# ---------- 3) RUNTIME -------------------------------------------------------
FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app
# Nie uruchamiamy aplikacji jako root
RUN addgroup -S -g 1000 app && adduser -S -u 1000 -G app app
COPY --from=backend --chown=app:app /workspace/app.jar app.jar
USER 1000:1000

# JVM respektuje limity pamięci kontenera (cgroups)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
