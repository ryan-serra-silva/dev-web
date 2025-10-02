FROM openjdk:17-slim

RUN apt-get update && \
    apt-get install -y maven make && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
