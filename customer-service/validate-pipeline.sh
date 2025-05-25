#!/bin/bash

# Customer Service Pipeline Validation Script
# This script simulates the CI/CD pipeline steps locally for development

set -e

echo "🚀 Customer Service Pipeline Validation"
echo "======================================"

# Check if we're in the right directory
if [ ! -f "customer-service/build.gradle" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    exit 1
fi

cd customer-service

echo ""
echo "📋 Pre-flight Checks"
echo "-------------------"

# Check Java version
echo "☕ Checking Java version..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        echo "✅ Java $JAVA_VERSION detected (required: 17+)"
    else
        echo "❌ Java 17+ required, found: $JAVA_VERSION"
        exit 1
    fi
else
    echo "❌ Java not found. Please install Java 17+"
    exit 1
fi

# Check Gradle wrapper
echo "🔧 Checking Gradle wrapper..."
if [ -f "./gradlew" ]; then
    chmod +x ./gradlew
    echo "✅ Gradle wrapper found and made executable"
else
    echo "❌ Gradle wrapper not found"
    exit 1
fi

# Check Docker
echo "🐳 Checking Docker..."
if command -v docker &> /dev/null; then
    if docker info >/dev/null 2>&1; then
        echo "✅ Docker is running"
    else
        echo "⚠️  Docker is installed but not running"
    fi
else
    echo "⚠️  Docker not found (needed for full pipeline)"
fi

echo ""
echo "🧪 Step 1: Running Tests"
echo "------------------------"
./gradlew test --info
echo "✅ Tests completed successfully"

echo ""
echo "🔨 Step 2: Building Application"
echo "------------------------------"
./gradlew bootJar --info
echo "✅ Build completed successfully"

echo ""
echo "📊 Step 3: Code Quality Checks"
echo "------------------------------"
./gradlew check --info
echo "✅ Code quality checks passed"

echo ""
echo "📁 Checking Build Artifacts"
echo "---------------------------"
if [ -f "build/libs/"*.jar ]; then
    echo "✅ JAR file(s) created:"
    ls -la build/libs/*.jar
else
    echo "❌ No JAR files found in build/libs/"
    exit 1
fi

echo ""
echo "🐳 Step 4: Docker Build Test (Optional)"
echo "---------------------------------------"
if command -v docker &> /dev/null && docker info >/dev/null 2>&1; then
    echo "Building Docker image..."
    docker build -t customer-service-test .
    echo "✅ Docker build successful"
    
    echo "Cleaning up test image..."
    docker rmi customer-service-test
    echo "✅ Test image removed"
else
    echo "⚠️  Skipping Docker build (Docker not available)"
fi

echo ""
echo "📋 Artifact Summary"
echo "------------------"
echo "JAR Files:"
ls -la build/libs/*.jar 2>/dev/null || echo "No JAR files found"

echo ""
echo "Test Results:"
if [ -d "build/test-results/test" ]; then
    TEST_COUNT=$(find build/test-results/test -name "*.xml" | wc -l)
    echo "Found $TEST_COUNT test result files"
else
    echo "No test results found"
fi

echo ""
echo "🎉 Pipeline Validation Complete!"
echo "==============================="
echo "All local checks passed. Your code is ready for the CI/CD pipeline."
echo ""
echo "Next steps:"
echo "1. Commit your changes"
echo "2. Push to your feature branch"
echo "3. Create a Pull Request to main"
echo "4. Monitor the GitHub Actions pipeline"

# Return to original directory
cd ..

echo ""
echo "For more information, see:"
echo "- customer-service/CI-CD-README.md"
echo "- .github/workflows/README.md" 