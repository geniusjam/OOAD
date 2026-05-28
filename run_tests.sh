#!/bin/bash
set -e

JUNIT_JAR=lib/junit-platform-console-standalone-1.11.4.jar

mkdir -p out/production out/test

echo "==> Compiling production sources..."
find src -name "*.java" | xargs javac -d out/production

echo "==> Compiling test sources..."
find test -name "*.java" | xargs javac -cp "$JUNIT_JAR:out/production" -d out/test

echo "==> Running tests..."
java -jar "$JUNIT_JAR" \
    --class-path "out/test:out/production" \
    --scan-class-path
