#!/bin/bash

# Log viewing utility for credential-generation service
# Usage: ./view-logs.sh [log-type] [options]

LOG_DIR="logs"
APP_NAME="credential-generation"

show_help() {
    echo "Log Viewer for Credential Generation Service"
    echo ""
    echo "Usage: $0 [log-type] [options]"
    echo ""
    echo "Log Types:"
    echo "  all         - View all logs (default)"
    echo "  credential  - View credential-specific logs"
    echo "  error       - View error logs only"
    echo "  live        - Live tail of all logs"
    echo "  live-cred   - Live tail of credential logs"
    echo "  live-error  - Live tail of error logs"
    echo ""
    echo "Options:"
    echo "  -h, --help  - Show this help message"
    echo "  -n NUM      - Show last NUM lines (default: 100)"
    echo ""
    echo "Examples:"
    echo "  $0                    # View last 100 lines of all logs"
    echo "  $0 credential         # View credential logs"
    echo "  $0 live              # Live tail all logs"
    echo "  $0 all -n 50         # View last 50 lines of all logs"
}

# Default values
LOG_TYPE="all"
LINES=100

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -n)
            LINES="$2"
            shift 2
            ;;
        all|credential|error|live|live-cred|live-error)
            LOG_TYPE="$1"
            shift
            ;;
        *)
            echo "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

# Create logs directory if it doesn't exist
mkdir -p "$LOG_DIR"

# Function to check if log file exists
check_log_file() {
    if [[ ! -f "$1" ]]; then
        echo "Log file not found: $1"
        echo "Make sure the application is running and has generated logs."
        exit 1
    fi
}

# Execute based on log type
case $LOG_TYPE in
    all)
        LOG_FILE="$LOG_DIR/$APP_NAME.log"
        check_log_file "$LOG_FILE"
        echo "=== Viewing last $LINES lines of all logs ==="
        tail -n "$LINES" "$LOG_FILE"
        ;;
    credential)
        LOG_FILE="$LOG_DIR/$APP_NAME-credentials.log"
        check_log_file "$LOG_FILE"
        echo "=== Viewing last $LINES lines of credential logs ==="
        tail -n "$LINES" "$LOG_FILE"
        ;;
    error)
        LOG_FILE="$LOG_DIR/$APP_NAME-errors.log"
        check_log_file "$LOG_FILE"
        echo "=== Viewing last $LINES lines of error logs ==="
        tail -n "$LINES" "$LOG_FILE"
        ;;
    live)
        LOG_FILE="$LOG_DIR/$APP_NAME.log"
        echo "=== Live tailing all logs (Press Ctrl+C to stop) ==="
        tail -f "$LOG_FILE"
        ;;
    live-cred)
        LOG_FILE="$LOG_DIR/$APP_NAME-credentials.log"
        echo "=== Live tailing credential logs (Press Ctrl+C to stop) ==="
        tail -f "$LOG_FILE"
        ;;
    live-error)
        LOG_FILE="$LOG_DIR/$APP_NAME-errors.log"
        echo "=== Live tailing error logs (Press Ctrl+C to stop) ==="
        tail -f "$LOG_FILE"
        ;;
esac 