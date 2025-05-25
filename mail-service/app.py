from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, EmailStr, Field
from typing import List, Optional
import logging
import datetime
import json
import uuid
import smtplib
import os
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from contextlib import asynccontextmanager
import asyncio
from concurrent.futures import ThreadPoolExecutor

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Email configuration from environment variables
SMTP_SERVER = os.getenv("SMTP_SERVER", "smtp.gmail.com")
SMTP_PORT = int(os.getenv("SMTP_PORT", "587"))
SMTP_USERNAME = os.getenv("SMTP_USERNAME", "")
SMTP_PASSWORD = os.getenv("SMTP_PASSWORD", "")
SMTP_USE_TLS = os.getenv("SMTP_USE_TLS", "true").lower() == "true"
DEFAULT_FROM_EMAIL = os.getenv("DEFAULT_FROM_EMAIL", "noreply@contractsigning.com")
SIMULATION_MODE = os.getenv("SIMULATION_MODE", "true").lower() == "true"

# Store sent emails in memory (in production, use a database)
sent_emails = []

# Thread pool for sending emails asynchronously
executor = ThreadPoolExecutor(max_workers=10)

class EmailRequest(BaseModel):
    to: List[EmailStr] = Field(
        ..., 
        description="List of recipient email addresses",
        example=["john.doe@example.com", "jane.smith@example.com"]
    )
    subject: str = Field(
        ..., 
        min_length=1, 
        max_length=200, 
        description="Email subject",
        example="Welcome to Contract Signing Platform"
    )
    body: str = Field(
        ..., 
        min_length=1, 
        description="Email body content (plain text)",
        example="Dear user, your account has been created successfully. Your credentials have been generated."
    )
    from_email: Optional[EmailStr] = Field(
        default=None, 
        description="Sender email address (optional, uses default if not provided)",
        example="admin@contractsigning.com"
    )
    html_body: Optional[str] = Field(
        default=None, 
        description="HTML version of email body (optional)",
        example="<h1>Welcome!</h1><p>Your account has been created successfully.</p>"
    )

    class Config:
        schema_extra = {
            "example": {
                "to": ["user@example.com"],
                "subject": "Your Contract Signing Credentials",
                "body": "Welcome! Your account has been created. Username: user123, Password: temp_pass",
                "from_email": "noreply@contractsigning.com",
                "html_body": "<h2>Welcome to Contract Signing</h2><p>Your credentials:<br><b>Username:</b> user123<br><b>Password:</b> temp_pass</p>"
            }
        }

class EmailResponse(BaseModel):
    message_id: str = Field(description="Unique message identifier", example="550e8400-e29b-41d4-a716-446655440000")
    status: str = Field(description="Email status", example="sent")
    to: List[str] = Field(description="Recipients", example=["user@example.com"])
    subject: str = Field(description="Email subject", example="Your Contract Signing Credentials")
    sent_at: datetime.datetime = Field(description="Timestamp when email was sent")
    message: str = Field(description="Status message", example="Email successfully sent to 1 recipient(s)")
    simulation_mode: bool = Field(description="Whether email was simulated or actually sent", example=True)

class EmailStatus(BaseModel):
    message_id: str = Field(description="Unique message identifier")
    status: str = Field(description="Email status (sent, failed, pending)")
    to: List[str] = Field(description="Recipients")
    subject: str = Field(description="Email subject")
    sent_at: datetime.datetime = Field(description="Timestamp when email was sent")
    delivered_at: Optional[datetime.datetime] = Field(default=None, description="Timestamp when email was delivered")
    error_message: Optional[str] = Field(default=None, description="Error message if sending failed")

def send_email_smtp(to_emails: List[str], subject: str, body: str, from_email: str, html_body: Optional[str] = None) -> tuple[bool, str]:
    """
    Send email using SMTP server.
    Returns (success: bool, error_message: str)
    """
    try:
        # Create message
        msg = MIMEMultipart('alternative')
        msg['Subject'] = subject
        msg['From'] = from_email
        msg['To'] = ', '.join(to_emails)
        
        # Add text body
        text_part = MIMEText(body, 'plain')
        msg.attach(text_part)
        
        # Add HTML body if provided
        if html_body:
            html_part = MIMEText(html_body, 'html')
            msg.attach(html_part)
        
        # Connect to SMTP server
        with smtplib.SMTP(SMTP_SERVER, SMTP_PORT) as server:
            if SMTP_USE_TLS:
                server.starttls()
            
            if SMTP_USERNAME and SMTP_PASSWORD:
                server.login(SMTP_USERNAME, SMTP_PASSWORD)
            
            # Send email
            server.send_message(msg, from_addr=from_email, to_addrs=to_emails)
        
        logger.info(f"Email sent successfully via SMTP to: {to_emails}")
        return True, ""
        
    except Exception as e:
        error_msg = f"SMTP Error: {str(e)}"
        logger.error(error_msg)
        return False, error_msg

def simulate_email_sending(to_emails: List[str], subject: str, body: str, from_email: str) -> tuple[bool, str]:
    """Simulate email sending for development/testing."""
    import time
    time.sleep(0.1 * len(to_emails))  # Simulate network delay
    logger.info(f"SIMULATED: Email sent to {to_emails}")
    return True, ""

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    logger.info("Real Mail Service starting up...")
    logger.info(f"SMTP Server: {SMTP_SERVER}:{SMTP_PORT}")
    logger.info(f"Simulation Mode: {SIMULATION_MODE}")
    logger.info(f"TLS Enabled: {SMTP_USE_TLS}")
    
    if not SIMULATION_MODE and not SMTP_USERNAME:
        logger.warning("SMTP credentials not configured - emails may fail!")
    
    yield
    # Shutdown
    logger.info("Real Mail Service shutting down...")
    executor.shutdown(wait=True)

# Enhanced FastAPI app with comprehensive OpenAPI documentation
app = FastAPI(
    title="📧 Mail Service API",
    description="""
    ## Contract Signing Mail Service
    
    A comprehensive mail service that can send emails to multiple recipients via SMTP or simulate sending for development.
    
    ### Features
    * 📨 **Send emails** to multiple recipients
    * 🔄 **Simulation mode** for development and testing
    * 📡 **Real SMTP** support for production
    * 📊 **Email tracking** with unique message IDs
    * 📈 **Email history** and status monitoring
    * 🏥 **Health checks** for service monitoring
    * 🔧 **Configuration management**
    
    ### Modes
    * **Simulation Mode** (`SIMULATION_MODE=true`): Logs email activity without sending real emails
    * **SMTP Mode** (`SIMULATION_MODE=false`): Sends real emails via configured SMTP server
    
    ### SMTP Providers Supported
    * Gmail, Outlook, Yahoo, Custom SMTP servers
    
    ### Security
    * Email validation, TLS encryption, secure authentication
    """,
    version="2.0.0",
    contact={
        "name": "Contract Signing Support",
        "email": "support@contractsigning.com",
    },
    license_info={
        "name": "MIT",
    },
    lifespan=lifespan,
    docs_url="/docs",  # Swagger UI
    redoc_url="/redoc",  # ReDoc
    openapi_url="/openapi.json"  # OpenAPI spec
)

@app.get(
    "/",
    summary="Service Information",
    description="Get basic information about the mail service including current configuration and status.",
    tags=["Service Info"]
)
async def root():
    """Get service information and current status."""
    return {
        "service": "Mail Service",
        "status": "running",
        "version": "2.0.0",
        "simulation_mode": SIMULATION_MODE,
        "smtp_server": SMTP_SERVER if not SIMULATION_MODE else "N/A",
        "description": "Sends real emails via SMTP or simulates sending",
        "docs_url": "/docs",
        "redoc_url": "/redoc"
    }

@app.post(
    "/api/mail/send", 
    response_model=EmailResponse,
    summary="Send Email",
    description="Send an email to one or more recipients. Supports both plain text and HTML content.",
    tags=["Email Operations"],
    responses={
        200: {
            "description": "Email sent successfully",
            "content": {
                "application/json": {
                    "example": {
                        "message_id": "550e8400-e29b-41d4-a716-446655440000",
                        "status": "sent",
                        "to": ["user@example.com"],
                        "subject": "Welcome Email",
                        "sent_at": "2025-05-25T21:30:00Z",
                        "message": "Email successfully sent to 1 recipient(s)",
                        "simulation_mode": True
                    }
                }
            }
        },
        500: {"description": "Email sending failed"}
    }
)
async def send_email(email_request: EmailRequest):
    """
    Send an email to multiple recipients.
    
    **Features:**
    - Multiple recipients support
    - Plain text and HTML content
    - Custom sender address
    - Automatic email validation
    - Unique message ID tracking
    
    **Modes:**
    - **Simulation**: Logs activity without sending real emails
    - **SMTP**: Sends real emails via configured SMTP server
    """
    message_id = str(uuid.uuid4())
    sent_at = datetime.datetime.utcnow()
    from_email = email_request.from_email or DEFAULT_FROM_EMAIL
    
    logger.info(f"Email send request - MessageId: {message_id}")
    logger.info(f"To: {email_request.to}")
    logger.info(f"Subject: {email_request.subject}")
    logger.info(f"From: {from_email}")
    logger.info(f"Simulation Mode: {SIMULATION_MODE}")
    
    try:
        to_emails = [str(email) for email in email_request.to]
        
        # Send email (real or simulated)
        if SIMULATION_MODE:
            success, error_msg = simulate_email_sending(
                to_emails, email_request.subject, email_request.body, from_email
            )
        else:
            # Send real email in thread pool to avoid blocking
            loop = asyncio.get_event_loop()
            success, error_msg = await loop.run_in_executor(
                executor, 
                send_email_smtp,
                to_emails,
                email_request.subject,
                email_request.body,
                from_email,
                email_request.html_body
            )
        
        if success:
            status = "sent"
            delivered_at = sent_at
            message = f"Email successfully {'simulated' if SIMULATION_MODE else 'sent'} to {len(to_emails)} recipient(s)"
        else:
            status = "failed"
            delivered_at = None
            message = f"Failed to send email: {error_msg}"
        
        # Store the email record
        email_record = EmailStatus(
            message_id=message_id,
            status=status,
            to=to_emails,
            subject=email_request.subject,
            sent_at=sent_at,
            delivered_at=delivered_at,
            error_message=error_msg if not success else None
        )
        sent_emails.append(email_record.dict())
        
        logger.info(f"Email {status} - MessageId: {message_id}, Recipients: {len(to_emails)}")
        
        return EmailResponse(
            message_id=message_id,
            status=status,
            to=to_emails,
            subject=email_request.subject,
            sent_at=sent_at,
            message=message,
            simulation_mode=SIMULATION_MODE
        )
        
    except Exception as e:
        logger.error(f"Failed to send email - MessageId: {message_id}, Error: {str(e)}")
        
        # Store failed email record
        email_record = EmailStatus(
            message_id=message_id,
            status="failed",
            to=[str(email) for email in email_request.to],
            subject=email_request.subject,
            sent_at=sent_at,
            error_message=str(e)
        )
        sent_emails.append(email_record.dict())
        
        raise HTTPException(status_code=500, detail=f"Failed to send email: {str(e)}")

@app.get(
    "/api/mail/status/{message_id}", 
    response_model=EmailStatus,
    summary="Get Email Status",
    description="Retrieve the status and details of a sent email by its message ID.",
    tags=["Email Operations"]
)
async def get_email_status(message_id: str):
    """Get the status of a sent email by message ID."""
    logger.info(f"Status request for MessageId: {message_id}")
    
    for email in sent_emails:
        if email["message_id"] == message_id:
            logger.info(f"Status found for MessageId: {message_id}")
            return EmailStatus(**email)
    
    logger.warning(f"MessageId not found: {message_id}")
    raise HTTPException(status_code=404, detail="Email not found")

@app.get(
    "/api/mail/history",
    summary="Get Email History",
    description="Retrieve a paginated list of sent emails with their status and details.",
    tags=["Email Operations"]
)
async def get_email_history(limit: int = Field(default=50, ge=1, le=1000, description="Maximum number of emails to return")):
    """Get the history of sent emails."""
    logger.info(f"Email history requested, limit: {limit}")
    
    recent_emails = sent_emails[-limit:] if len(sent_emails) > limit else sent_emails
    
    return {
        "total_sent": len(sent_emails),
        "returned_count": len(recent_emails),
        "simulation_mode": SIMULATION_MODE,
        "emails": recent_emails
    }

@app.get(
    "/api/mail/health",
    summary="Health Check",
    description="Health check endpoint for monitoring service status and basic metrics.",
    tags=["Monitoring"]
)
async def health_check():
    """Health check endpoint for monitoring."""
    return {
        "status": "healthy",
        "timestamp": datetime.datetime.utcnow(),
        "total_emails_sent": len(sent_emails),
        "simulation_mode": SIMULATION_MODE,
        "smtp_configured": bool(SMTP_USERNAME) if not SIMULATION_MODE else None,
        "service": "mail-service"
    }

@app.get(
    "/api/mail/config",
    summary="Get Configuration",
    description="Get current mail service configuration (sensitive data excluded).",
    tags=["Configuration"]
)
async def get_config():
    """Get current mail service configuration (without sensitive data)."""
    return {
        "simulation_mode": SIMULATION_MODE,
        "smtp_server": SMTP_SERVER if not SIMULATION_MODE else "N/A",
        "smtp_port": SMTP_PORT if not SIMULATION_MODE else "N/A",
        "smtp_tls": SMTP_USE_TLS if not SIMULATION_MODE else "N/A",
        "smtp_username_configured": bool(SMTP_USERNAME),
        "default_from_email": DEFAULT_FROM_EMAIL
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8083) 