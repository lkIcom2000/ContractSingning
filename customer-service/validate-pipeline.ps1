# Customer Service Pipeline Validation Script (PowerShell)
# This script simulates the CI/CD pipeline steps locally for development on Windows

param(
    [switch]$SkipDocker = $false
)

Write-Host "🚀 Customer Service Pipeline Validation" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green

# Check if we're in the right directory
if (-not (Test-Path "customer-service\build.gradle")) {
    Write-Host "❌ Error: Please run this script from the project root directory" -ForegroundColor Red
    exit 1
}

Set-Location customer-service

Write-Host ""
Write-Host "📋 Pre-flight Checks" -ForegroundColor Yellow
Write-Host "-------------------" -ForegroundColor Yellow

# Check Java version
Write-Host "☕ Checking Java version..."
try {
    $javaVersion = & java -version 2>&1
    $versionLine = $javaVersion[0] -replace '.*"([^"]*)".*', '$1'
    $majorVersion = ($versionLine -split '\.')[0]
    
    if ([int]$majorVersion -ge 17) {
        Write-Host "✅ Java $majorVersion detected (required: 17+)" -ForegroundColor Green
    } else {
        Write-Host "❌ Java 17+ required, found: $majorVersion" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Java not found. Please install Java 17+" -ForegroundColor Red
    exit 1
}

# Check Gradle wrapper
Write-Host "🔧 Checking Gradle wrapper..."
if (Test-Path "gradlew.bat") {
    Write-Host "✅ Gradle wrapper found" -ForegroundColor Green
} else {
    Write-Host "❌ Gradle wrapper not found" -ForegroundColor Red
    exit 1
}

# Check Docker
Write-Host "🐳 Checking Docker..."
if (-not $SkipDocker) {
    try {
        $dockerInfo = & docker info 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Docker is running" -ForegroundColor Green
        } else {
            Write-Host "⚠️  Docker is installed but not running" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "⚠️  Docker not found (needed for full pipeline)" -ForegroundColor Yellow
    }
} else {
    Write-Host "⚠️  Docker checks skipped" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🧪 Step 1: Running Tests" -ForegroundColor Cyan
Write-Host "------------------------" -ForegroundColor Cyan
try {
    & .\gradlew.bat test --info
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Tests completed successfully" -ForegroundColor Green
    } else {
        Write-Host "❌ Tests failed" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Error running tests: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🔨 Step 2: Building Application" -ForegroundColor Cyan
Write-Host "------------------------------" -ForegroundColor Cyan
try {
    & .\gradlew.bat bootJar --info
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Build completed successfully" -ForegroundColor Green
    } else {
        Write-Host "❌ Build failed" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Error building application: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📊 Step 3: Code Quality Checks" -ForegroundColor Cyan
Write-Host "------------------------------" -ForegroundColor Cyan
try {
    & .\gradlew.bat check --info
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Code quality checks passed" -ForegroundColor Green
    } else {
        Write-Host "❌ Code quality checks failed" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Error running quality checks: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📁 Checking Build Artifacts" -ForegroundColor Yellow
Write-Host "---------------------------" -ForegroundColor Yellow
$jarFiles = Get-ChildItem "build\libs\*.jar" -ErrorAction SilentlyContinue
if ($jarFiles) {
    Write-Host "✅ JAR file(s) created:" -ForegroundColor Green
    $jarFiles | ForEach-Object { Write-Host "  $($_.Name) ($([math]::Round($_.Length / 1MB, 2)) MB)" }
} else {
    Write-Host "❌ No JAR files found in build\libs\" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🐳 Step 4: Docker Build Test (Optional)" -ForegroundColor Cyan
Write-Host "---------------------------------------" -ForegroundColor Cyan
if (-not $SkipDocker) {
    try {
        $dockerInfo = & docker info 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Building Docker image..."
            & docker build -t customer-service-test .
            if ($LASTEXITCODE -eq 0) {
                Write-Host "✅ Docker build successful" -ForegroundColor Green
                
                Write-Host "Cleaning up test image..."
                & docker rmi customer-service-test
                Write-Host "✅ Test image removed" -ForegroundColor Green
            } else {
                Write-Host "❌ Docker build failed" -ForegroundColor Red
            }
        } else {
            Write-Host "⚠️  Skipping Docker build (Docker not running)" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "⚠️  Skipping Docker build (Docker not available)" -ForegroundColor Yellow
    }
} else {
    Write-Host "⚠️  Docker build skipped (use -SkipDocker to avoid this)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "📋 Artifact Summary" -ForegroundColor Yellow
Write-Host "------------------" -ForegroundColor Yellow
Write-Host "JAR Files:"
$jarFiles = Get-ChildItem "build\libs\*.jar" -ErrorAction SilentlyContinue
if ($jarFiles) {
    $jarFiles | ForEach-Object { 
        $sizeInMB = [math]::Round($_.Length / 1MB, 2)
        Write-Host "  ✅ $($_.Name) ($sizeInMB MB)" -ForegroundColor Green
    }
} else {
    Write-Host "  ❌ No JAR files found" -ForegroundColor Red
}

Write-Host ""
Write-Host "Test Results:"
if (Test-Path "build\test-results\test") {
    $testFiles = Get-ChildItem "build\test-results\test\*.xml" -ErrorAction SilentlyContinue
    $testCount = if ($testFiles) { $testFiles.Count } else { 0 }
    Write-Host "  ✅ Found $testCount test result files" -ForegroundColor Green
} else {
    Write-Host "  ❌ No test results found" -ForegroundColor Red
}

Write-Host ""
Write-Host "🎉 Pipeline Validation Complete!" -ForegroundColor Green
Write-Host "===============================" -ForegroundColor Green
Write-Host "All local checks passed. Your code is ready for the CI/CD pipeline." -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Commit your changes"
Write-Host "2. Push to your feature branch"
Write-Host "3. Create a Pull Request to main"
Write-Host "4. Monitor the GitHub Actions pipeline"

# Return to original directory
Set-Location ..

Write-Host ""
Write-Host "For more information, see:" -ForegroundColor Yellow
Write-Host "- customer-service\CI-CD-README.md"
Write-Host "- .github\workflows\README.md"
Write-Host ""
Write-Host "Usage examples:" -ForegroundColor Yellow
Write-Host "  .\customer-service\validate-pipeline.ps1           # Full validation"
Write-Host "  .\customer-service\validate-pipeline.ps1 -SkipDocker  # Skip Docker build" 