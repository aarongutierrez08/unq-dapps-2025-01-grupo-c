# Etapa 1: Build
FROM amazoncorretto:21 as build

WORKDIR /app
COPY . .
RUN ./gradlew build -x test --no-daemon

# Etapa 2: Run
FROM amazoncorretto:21

RUN yum update -y && \
    yum install -y wget unzip libX11 curl sed grep && \
    wget https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm && \
    yum install -y ./google-chrome-stable_current_x86_64.rpm && \
    rm google-chrome-stable_current_x86_64.rpm && \
    CHROME_VERSION=$(google-chrome --version | grep -oP "\d+\.\d+\.\d+\.\d+") && \
    echo "Installed Chrome version: $CHROME_VERSION" && \
    CHROMEDRIVER_URL=$(curl -s "https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions-with-downloads.json" | \
      grep -A 10 "\"$CHROME_VERSION\"" | grep "linux64" | grep "chromedriver-linux64.zip" | sed -n 's/.*"url": "\(.*\)".*/\1/p') && \
    echo "Downloading Chromedriver from: $CHROMEDRIVER_URL" && \
    wget "$CHROMEDRIVER_URL" -O chromedriver.zip && \
    unzip chromedriver.zip && \
    mv chromedriver-linux64/chromedriver /usr/bin/chromedriver && \
    chmod +x /usr/bin/chromedriver && \
    rm -rf chromedriver-linux64 chromedriver.zip

# Variables de entorno para Selenium
ENV CHROME_BIN="/usr/bin/google-chrome"
ENV PATH="/usr/bin:${PATH}"

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
