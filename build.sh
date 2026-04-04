#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR/galactype"
SUBMIT_DIR="$SCRIPT_DIR/submission"

JAR_OUT="$PROJECT_DIR/target/galactype.jar"
JAVADOC_ZIP="$SUBMIT_DIR/galactype-javadoc.zip"
REPO_ZIP="$SUBMIT_DIR/galactype-repository.zip"

mkdir -p "$SUBMIT_DIR"

# 1. Build fat JAR  
echo "==> Building fat JAR..."
cd "$PROJECT_DIR"
mvn package -DskipTests
cp "$JAR_OUT" "$SUBMIT_DIR/galactype.jar"
echo "    JAR -> submission/galactype.jar"

# 2. Generate Javadoc 
echo ""
echo "==> Generating Javadoc..."
mvn javadoc:javadoc
cd "$PROJECT_DIR/target/reports/apidocs"
zip -r "$JAVADOC_ZIP" . -x "*.DS_Store"
echo "    Javadoc -> submission/galactype-javadoc.zip"

# 3. ZIP the repository 
echo ""
echo "==> Creating repository ZIP..."
cd "$SCRIPT_DIR"
zip -r "$REPO_ZIP" . \
    --exclude "*/.git/*" \
    --exclude "*/submission/*" \
    --exclude "*/.DS_Store" \
    --exclude "*/dependency-reduced-pom.xml"
echo "    Repo -> submission/galactype-repository.zip"

# Summary 
echo ""
echo "Done! Submission files:"
echo "  submission/galactype.jar"
echo "  submission/galactype-javadoc.zip"
echo "  submission/galactype-repository.zip"
