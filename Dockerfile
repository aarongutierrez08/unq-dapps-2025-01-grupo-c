# Etapa 1: Build
FROM amazoncorretto:21 as build

WORKDIR /app

COPY . .

RUN ./gradlew build --no-daemon

# Etapa 2: Run
FROM amazoncorretto:21

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
