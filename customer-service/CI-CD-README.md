# Customer Service CI/CD Pipeline

## Overview

This GitHub Actions workflow provides a comprehensive CI/CD pipeline for the Customer Service Spring Boot application. The pipeline ensures code quality, security, and automated deployment through multiple stages.

## Pipeline Architecture

### Trigger Conditions
- **Push Events**: Triggers on `main` and `develop` branches
- **Pull Request Events**: Triggers on PRs to `main` branch
- **Path Filtering**: Only runs when changes are made to:
  - `customer-service/**` directory
  - The pipeline file itself

### Jobs Overview

#### 1. **Test Job** 🧪
- **Purpose**: Runs unit tests and generates test reports
- **Environment**: Ubuntu Latest with JDK 17
- **Key Features**:
  - Gradle caching for faster builds
  - JUnit test execution
  - Test result reporting via GitHub UI
  - Test artifact upload for debugging

#### 2. **Build Job** 🔨
- **Purpose**: Compiles the application and creates JAR artifacts
- **Dependencies**: Runs after successful tests
- **Key Features**:
  - Builds Spring Boot JAR using `bootJar` task
  - Uploads build artifacts
  - Gradle dependency caching

#### 3. **Code Quality Job** 📊
- **Purpose**: Performs static code analysis and quality checks
- **Dependencies**: Runs after successful tests
- **Key Features**:
  - Runs `gradle check` for quality analysis
  - Identifies potential code issues
  - Ensures coding standards compliance

#### 4. **Docker Job** 🐳
- **Purpose**: Builds and pushes Docker images to GitHub Container Registry
- **Trigger Condition**: Only on pushes to `main` branch
- **Dependencies**: Requires successful test and build jobs
- **Key Features**:
  - Multi-tag Docker image creation
  - GitHub Container Registry integration
  - Docker layer caching for efficiency
  - Automatic versioning with Git SHA and branch names

#### 5. **Security Scan Job** 🔒
- **Purpose**: Scans Docker images for security vulnerabilities
- **Dependencies**: Runs after successful Docker build
- **Key Features**:
  - Trivy vulnerability scanning
  - SARIF format security reports
  - Integration with GitHub Security tab
  - Automated security alerts

## Configuration Details

### Environment Variables
```yaml
REGISTRY: ghcr.io
IMAGE_NAME: ${{ github.repository }}/customer-service
```

### Docker Image Tagging Strategy
- `latest`: For main branch pushes
- `<branch-name>`: For feature branch pushes
- `<branch>-<sha>`: Unique SHA-based tags
- PR numbers for pull requests

### Caching Strategy
- **Gradle Dependencies**: Cached based on gradle files hash
- **Docker Layers**: GitHub Actions cache for faster builds

## Prerequisites

### Repository Setup
1. Enable GitHub Actions in repository settings
2. Ensure proper branch protection rules for `main`
3. Configure GitHub Container Registry access

### Required Permissions
The pipeline requires the following permissions:
- `actions: read` - For workflow execution
- `contents: read` - For repository checkout
- `packages: write` - For container registry push
- `security-events: write` - For security scan results

### Secret Configuration
No additional secrets required - uses `GITHUB_TOKEN` automatically provided by GitHub Actions.

## Usage Instructions

### Automatic Triggers
1. **Development**: Push code to `develop` branch
   - Runs tests, build, and quality checks
   - No Docker image creation

2. **Production**: Push code to `main` branch
   - Runs full pipeline including Docker build and security scan
   - Publishes Docker image to GitHub Container Registry

3. **Pull Requests**: Create PR to `main`
   - Runs tests and quality checks
   - Provides feedback before merge

### Manual Workflow Dispatch
The workflow can be manually triggered from the GitHub Actions tab if needed.

## Monitoring and Troubleshooting

### Viewing Results
- **Test Results**: Available in Actions tab with detailed JUnit reports
- **Build Artifacts**: Downloadable JAR files from Actions summary
- **Security Reports**: Visible in Security tab under Code scanning alerts
- **Docker Images**: Available in Packages section of repository

### Common Issues and Solutions

#### 1. Test Failures
- Check test logs in the Actions tab
- Download test artifacts for detailed analysis
- Ensure database connectivity in tests

#### 2. Build Issues
- Verify Gradle wrapper permissions
- Check Java version compatibility
- Review dependency conflicts

#### 3. Docker Build Problems
- Validate Dockerfile syntax
- Ensure JAR file is properly built
- Check base image availability

#### 4. Security Scan Failures
- Review Trivy scan results in Security tab
- Update dependencies to fix vulnerabilities
- Consider using different base images

## Performance Optimizations

### Caching
- Gradle dependencies cached between runs
- Docker layers cached for faster builds
- Cache keys based on file hashes for accuracy

### Parallel Execution
- Test and build jobs run independently after test completion
- Code quality analysis runs in parallel with build

### Resource Efficiency
- Path filtering prevents unnecessary runs
- Conditional job execution for production workflows

## Integration with Development Workflow

### Branch Strategy Compatibility
- Works with GitFlow, GitHub Flow, or similar strategies
- Supports feature branches through path filtering
- Provides different behavior for development vs production branches

### Code Quality Gates
- Prevents merging if tests fail
- Ensures code quality standards are met
- Provides security vulnerability reporting

## Future Enhancements

### Potential Additions
1. **Performance Testing**: JMeter or similar tools
2. **Integration Testing**: Database and API integration tests
3. **Deployment Automation**: Kubernetes or cloud deployment
4. **Notification Integration**: Slack, Email, or Teams notifications
5. **Code Coverage**: Jacoco coverage reports and thresholds

### Extensibility
The pipeline is designed to be easily extended with additional jobs or steps as the project requirements evolve.

## Support and Maintenance

### Pipeline Updates
- Pipeline configuration is version-controlled
- Changes follow same review process as code changes
- Backward compatibility maintained when possible

### Documentation
- Keep this README updated with pipeline changes
- Document any custom configurations or secrets
- Maintain troubleshooting guides based on common issues

---

For questions or issues with the CI/CD pipeline, please create an issue in the repository or contact the DevOps team. 