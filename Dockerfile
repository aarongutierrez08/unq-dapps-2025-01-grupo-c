# Etapa 1: Build
FROM amazoncorretto:21 as build

WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

# Etapa 2: Run
FROM amazoncorretto:21

# Instala dependencias necesarias para Chrome y ChromeDriver (para Debian/Ubuntu)
RUN yum update -y && \
    yum install -y wget unzip libX11 && \
    wget https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm && \
    yum install -y ./google-chrome-stable_current_x86_64.rpm && \
    rm google-chrome-stable_current_x86_64.rpm && \
    wget https://storage.googleapis.com/chrome-for-testing-public/135.0.7049.84/linux64/chromedriver-linux64.zip && \
    unzip chromedriver_linux64.zip && \
    mv chromedriver /usr/bin/chromedriver && \
    chmod +x /usr/bin/chromedriver && \
    rm chromedriver_linux64.zip

# Variables de entorno para Selenium
ENV CHROME_BIN="/usr/bin/google-chrome"
ENV PATH="/usr/bin:${PATH}"

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
