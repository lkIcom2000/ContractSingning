# Logging Documentation

## Overview

The Credential Generation Service now includes a comprehensive logging system using SLF4J with Logback. This replaces the previous `System.out.println` statements with structured, configurable logging.

## Log Files

The application generates several log files in the `logs/` directory:

- **`credential-generation.log`** - Main application log containing all activity
- **`credential-generation-credentials.log`** - Dedicated log for credential operations (generation, verification)
- **`credential-generation-errors.log`** - Error-only log for troubleshooting

## Log Levels

The application uses the following log levels:

- **ERROR** - Critical errors that require attention
- **WARN** - Warning messages about potential issues
- **INFO** - General information about application flow
- **DEBUG** - Detailed debugging information

## Configuration

### Application Properties
Key logging settings in `application.properties`:

```properties
# Logging Configuration
logging.level.dk.au.credentialgeneration=INFO
logging.level.org.springframework.web.reactive=INFO
logging.level.reactor.netty=INFO
logging.level.io.netty=WARN

# File logging configuration
logging.file.name=logs/credential-generation.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.console=%clr(%d{HH:mm:ss.SSS}){faint} %clr(%5p) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wEx
```

### Logback Configuration
The `logback-spring.xml` file provides advanced configuration including:

- **Colored console output** for better readability
- **Rolling file appenders** to manage log file sizes
- **Separate appenders** for different log types
- **Profile-specific configurations** (dev/prod)

## Log Features

### Request Tracing
Each request is assigned a unique 8-character request ID for tracing:

```
INFO - Starting credential generation request - RequestId: a1b2c3d4, CustomerId: 123
INFO - Customer data retrieved successfully - RequestId: a1b2c3d4, CustomerName: John Doe
INFO - Credentials generated - RequestId: a1b2c3d4, Username: john.doe, PasswordLength: 8
INFO - Credential generation completed successfully - RequestId: a1b2c3d4, CustomerId: 123
```

### Security Considerations
- **Passwords are never logged** in plain text
- **Hash prefixes** are logged for debugging (first 10 characters only)
- **Invalid password attempts** are logged as warnings
- **Password lengths** are logged instead of actual passwords

### Error Handling
Comprehensive error logging includes:
- **Request context** (RequestId, CustomerId)
- **Error messages** with full context
- **Stack traces** for debugging
- **Separate error log file** for easy monitoring

## Viewing Logs

### Using Log Viewer Scripts

#### PowerShell (Windows)
```powershell
# View all logs (last 100 lines)
.\view-logs.ps1

# View credential-specific logs
.\view-logs.ps1 credential

# View error logs only
.\view-logs.ps1 error

# Live tail all logs
.\view-logs.ps1 live

# View last 50 lines
.\view-logs.ps1 all -Lines 50

# Get help
.\view-logs.ps1 -Help
```

#### Bash (Linux/macOS)
```bash
# View all logs (last 100 lines)
./view-logs.sh

# View credential-specific logs
./view-logs.sh credential

# View error logs only
./view-logs.sh error

# Live tail all logs
./view-logs.sh live

# View last 50 lines
./view-logs.sh all -n 50

# Get help
./view-logs.sh --help
```

### Manual Log Viewing

#### Windows PowerShell
```powershell
# View latest logs
Get-Content logs\credential-generation.log -Tail 100

# Live tail logs
Get-Content logs\credential-generation.log -Wait -Tail 10

# Search for specific patterns
Select-String -Path logs\credential-generation.log -Pattern "ERROR"
```

#### Linux/macOS
```bash
# View latest logs
tail -n 100 logs/credential-generation.log

# Live tail logs
tail -f logs/credential-generation.log

# Search for specific patterns
grep "ERROR" logs/credential-generation.log
```

## Log Rotation

Logs are automatically rotated based on:
- **File size**: Maximum 10MB per file
- **Time**: Daily rotation
- **Retention**: 
  - General logs: 30 days
  - Credential logs: 60 days
  - Error logs: 90 days
- **Total size caps** to prevent disk space issues

## Monitoring Endpoints

The application exposes logging endpoints via Spring Actuator:

- **Health**: `GET /actuator/health`
- **Logger levels**: `GET /actuator/loggers`
- **Change log level**: `POST /actuator/loggers/{logger-name}`

### Example: Change Log Level
```bash
# Set credential controller to DEBUG level
curl -X POST http://localhost:8082/actuator/loggers/dk.au.credentialgeneration.controller.CredentialController \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

## Troubleshooting

### Common Issues

1. **No log files created**
   - Ensure the `logs/` directory exists
   - Check file permissions
   - Verify the application is running

2. **Log files too large**
   - Logs rotate automatically
   - Check disk space
   - Consider adjusting retention policies in `logback-spring.xml`

3. **Missing log entries**
   - Check log level configuration
   - Verify logger names in `logback-spring.xml`
   - Use actuator endpoints to check current log levels

### Debug Mode
To enable debug logging temporarily:

```properties
# Add to application.properties
logging.level.dk.au.credentialgeneration=DEBUG
```

Or use the actuator endpoint to change at runtime without restart.

## Best Practices

1. **Monitor error logs** regularly for issues
2. **Use request IDs** to trace specific operations
3. **Adjust log levels** based on environment (INFO for prod, DEBUG for dev)
4. **Set up log aggregation** for production environments
5. **Monitor disk space** used by log files
6. **Use structured logging** for better parsing and analysis

## Performance Impact

- **Logging overhead** is minimal due to SLF4J's lazy evaluation
- **File I/O** is buffered for better performance
- **Async logging** can be enabled for high-throughput scenarios
- **Log level filtering** reduces unnecessary log generation 