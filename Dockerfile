# Etapa 1: Build
FROM amazoncorretto:21 as build

WORKDIR /app
COPY . .
RUN ./gradlew build -x test --no-daemon

# Etapa 2: Run
FROM amazoncorretto:21

RUN yum update -y && \
    yum install -y wget unzip libX11 && \
    wget https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm && \
    yum install -y ./google-chrome-stable_current_x86_64.rpm && \
    rm google-chrome-stable_current_x86_64.rpm && \
    wget https://storage.googleapis.com/chrome-for-testing-public/137.0.7151.119/linux64/chromedriver-linux64.zip && \
    unzip chromedriver-linux64.zip && \
    mv chromedriver-linux64/chromedriver /usr/bin/chromedriver && \
    chmod +x /usr/bin/chromedriver && \
    rm -rf chromedriver-linux64 chromedriver_linux64.zip

# Variables de entorno para Selenium
ENV CHROME_BIN="/usr/bin/google-chrome"
ENV PATH="/usr/bin:${PATH}"

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
