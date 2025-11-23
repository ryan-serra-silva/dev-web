FROM eclipse-temurin:17-jdk

RUN apt-get update && \
    apt-get install -y maven make && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
