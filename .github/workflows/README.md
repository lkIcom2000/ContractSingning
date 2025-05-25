# GitHub Actions Workflows

This directory contains automated CI/CD workflows for the ContractSigning project.

## Available Workflows

### 1. Customer Service CI/CD (`customer-service-ci-cd.yml`)
**Purpose**: Comprehensive CI/CD pipeline for the Spring Boot customer service  
**Triggers**: Push to main/develop, PRs to main, changes in customer-service/  
**Features**:
- ✅ Unit testing with JUnit reports
- 🔨 Gradle build with artifact upload
- 📊 Code quality analysis
- 🐳 Docker image build and push (main branch only)
- 🔒 Security vulnerability scanning

**Status Badge**:
```markdown
![Customer Service CI/CD](https://github.com/{username}/{repository}/actions/workflows/customer-service-ci-cd.yml/badge.svg)
```

### 2. General CI/CD (`ci-cd.yml`)
**Purpose**: Python-based CI/CD workflow  
**Triggers**: Push to main  
**Features**:
- 🐍 Python 3.10 setup
- 📦 Dependency installation
- 🧪 pytest testing

## Workflow Usage

### For Developers
1. **Feature Development**: Work on feature branches
2. **Testing**: Create PR to main branch to run tests
3. **Production**: Merge to main to trigger full pipeline

### For DevOps
- Monitor workflow runs in Actions tab
- Review security scan results in Security tab
- Access Docker images in Packages section

## Quick Commands

```bash
# Check workflow status
gh workflow list

# View recent runs
gh run list

# Watch a workflow run
gh run watch
```

## Configuration

### Branch Protection Rules
Recommended settings for main branch:
- Require status checks before merging
- Require branches to be up to date
- Require review from code owners

### Required Secrets
Most workflows use `GITHUB_TOKEN` (automatically provided).
Additional secrets may be needed for:
- External registry authentication
- Deployment credentials
- Notification services

## Support

For workflow issues:
1. Check the Actions tab for detailed logs
2. Review the specific workflow README files
3. Create an issue with the `ci/cd` label 