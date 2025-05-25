param(
    [Parameter(Position=0)]
    [string]$LogType = "all",
    
    [Parameter()]
    [int]$Lines = 100,
    
    [Parameter()]
    [switch]$Help
)

$LogDir = "logs"
$AppName = "credential-generation"

function Show-Help {
    Write-Host "Log Viewer for Credential Generation Service" -ForegroundColor Green
    Write-Host ""
    Write-Host "Usage: .\view-logs.ps1 [log-type] [-Lines NUM] [-Help]"
    Write-Host ""
    Write-Host "Log Types:" -ForegroundColor Yellow
    Write-Host "  all         - View all logs (default)"
    Write-Host "  credential  - View credential-specific logs"
    Write-Host "  error       - View error logs only"
    Write-Host "  live        - Live tail of all logs"
    Write-Host "  live-cred   - Live tail of credential logs"
    Write-Host "  live-error  - Live tail of error logs"
    Write-Host ""
    Write-Host "Parameters:" -ForegroundColor Yellow
    Write-Host "  -Lines NUM  - Show last NUM lines (default: 100)"
    Write-Host "  -Help       - Show this help message"
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Cyan
    Write-Host "  .\view-logs.ps1                    # View last 100 lines of all logs"
    Write-Host "  .\view-logs.ps1 credential         # View credential logs"
    Write-Host "  .\view-logs.ps1 live              # Live tail all logs"
    Write-Host "  .\view-logs.ps1 all -Lines 50     # View last 50 lines of all logs"
}

if ($Help) {
    Show-Help
    return
}

# Create logs directory if it doesn't exist
if (!(Test-Path $LogDir)) {
    New-Item -ItemType Directory -Path $LogDir -Force | Out-Null
}

# Function to check if log file exists
function Test-LogFile {
    param([string]$LogFile)
    
    if (!(Test-Path $LogFile)) {
        Write-Host "Log file not found: $LogFile" -ForegroundColor Red
        Write-Host "Make sure the application is running and has generated logs." -ForegroundColor Yellow
        return $false
    }
    return $true
}

# Execute based on log type
switch ($LogType.ToLower()) {
    "all" {
        $LogFile = Join-Path $LogDir "$AppName.log"
        if (Test-LogFile $LogFile) {
            Write-Host "=== Viewing last $Lines lines of all logs ===" -ForegroundColor Green
            Get-Content $LogFile -Tail $Lines
        }
    }
    "credential" {
        $LogFile = Join-Path $LogDir "$AppName-credentials.log"
        if (Test-LogFile $LogFile) {
            Write-Host "=== Viewing last $Lines lines of credential logs ===" -ForegroundColor Green
            Get-Content $LogFile -Tail $Lines
        }
    }
    "error" {
        $LogFile = Join-Path $LogDir "$AppName-errors.log"
        if (Test-LogFile $LogFile) {
            Write-Host "=== Viewing last $Lines lines of error logs ===" -ForegroundColor Green
            Get-Content $LogFile -Tail $Lines
        }
    }
    "live" {
        $LogFile = Join-Path $LogDir "$AppName.log"
        if (Test-LogFile $LogFile) {
            Write-Host "=== Live tailing all logs (Press Ctrl+C to stop) ===" -ForegroundColor Green
            Get-Content $LogFile -Wait -Tail 10
        }
    }
    "live-cred" {
        $LogFile = Join-Path $LogDir "$AppName-credentials.log"
        if (Test-LogFile $LogFile) {
            Write-Host "=== Live tailing credential logs (Press Ctrl+C to stop) ===" -ForegroundColor Green
            Get-Content $LogFile -Wait -Tail 10
        }
    }
    "live-error" {
        $LogFile = Join-Path $LogDir "$AppName-errors.log"
        if (Test-LogFile $LogFile) {
            Write-Host "=== Live tailing error logs (Press Ctrl+C to stop) ===" -ForegroundColor Green
            Get-Content $LogFile -Wait -Tail 10
        }
    }
    default {
        Write-Host "Unknown log type: $LogType" -ForegroundColor Red
        Show-Help
    }
} 