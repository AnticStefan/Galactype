#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR/galactype"
OUTPUT="$PROJECT_DIR/target/galactype.jar"

echo "Building Galactype fat JAR..."
cd "$PROJECT_DIR"
mvn package -DskipTests

echo ""
echo "Done! JAR saved to:"
echo "  $OUTPUT"
echo ""
echo "Run with:  java -jar \"$OUTPUT\""
