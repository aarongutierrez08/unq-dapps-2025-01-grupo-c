# Etapa 1: Build
FROM amazoncorretto:21 as build

WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

# Etapa 2: Run
FROM amazoncorretto:21

# Instala dependencias necesarias para Chrome y ChromeDriver (para Debian/Ubuntu)
RUN apt-get update -y && \
    apt-get install -y wget unzip libx11-6 libglib2.0-0 libnss3 libxcomposite1 libxcursor1 libxi6 libxtst6 libasound2 && \
    wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    apt-get install -y ./google-chrome-stable_current_amd64.deb && \
    rm google-chrome-stable_current_amd64.deb && \
    wget https://storage.googleapis.com/chrome-for-testing-public/135.0.7049.84/linux64/chromedriver-linux64.zip && \
    unzip chromedriver-linux64.zip && \
    mv chromedriver-linux64/chromedriver /usr/bin/chromedriver && \
    chmod +x /usr/bin/chromedriver && \
    rm -rf chromedriver-linux64 chromedriver-linux64.zip

# Variables de entorno para Selenium
ENV CHROME_BIN="/usr/bin/google-chrome"
ENV PATH="/usr/bin:${PATH}"

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
