#!/usr/bin/env bash
echo "Loading env vars"
. .env

echo "Starting database....(docker)"
cd docker-services
docker-compose down || true
docker-compose up -d || true
cd ..

echo "Running migrations...(flyway)"
flyway clean || true
flyway migrate || true