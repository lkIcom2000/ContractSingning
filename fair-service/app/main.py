import fastapi
import uvicorn
import psycopg2 
from psycopg2.extras import RealDictCursor 
from pydantic import BaseModel
from fastapi.responses import JSONResponse
import os 


app = fastapi.FastAPI()

# Database connection details - preferably use environment variables
DB_NAME = os.getenv("DB_NAME", "fair_db")
DB_USER = os.getenv("DB_USER", "fair_user")
DB_PASSWORD = os.getenv("DB_PASSWORD", "fair_password")
DB_HOST = os.getenv("DB_HOST", "fair-db") 
DB_PORT = os.getenv("DB_PORT", "5432")

def get_db_connection():
    conn = psycopg2.connect(dbname=DB_NAME, user=DB_USER, password=DB_PASSWORD, host=DB_HOST, port=DB_PORT)
    return conn

class FairData(BaseModel):
    fair_id: int
    square_meters: int
    hall_id: int


class ResponseMessage(BaseModel):
    message: str
    error_code: int | None = None

@app.post("/api/fairs", response_model=ResponseMessage) # Corrected path to include leading slash
def process_fair_data(data: FairData):
    print(f"Received data: {data}")
    
    try:
        conn = get_db_connection()
        cursor = conn.cursor(cursor_factory=RealDictCursor)
        
        # Check if the fair exists and the hall exists for that fair
        cursor.execute(
            "SELECT available_square_meters FROM halls WHERE fair_id = %s AND hall_id = %s",
            (data.fair_id, data.hall_id)
        )
        hall_info = cursor.fetchone()
        
        cursor.close()
        conn.close()
        
        if not hall_info:
            return JSONResponse(status_code=404, content=ResponseMessage(message=f"Fair ID {data.fair_id} with Hall ID {data.hall_id} not found.").model_dump())

        available_space = hall_info['available_square_meters']
        
        if data.square_meters <= available_space:
            # Optionally, you might want to update the available_square_meters here if a booking is made
            return ResponseMessage(message=f"Space is available in Hall ID {data.hall_id} for Fair ID {data.fair_id}.")
        else:
            return JSONResponse(status_code=400, content=ResponseMessage(message=f"Not enough space in Hall ID {data.hall_id}. Available: {available_space} sqm, Requested: {data.square_meters} sqm.").model_dump())

    except psycopg2.Error as e:
        print(f"Database error: {e}")
        return JSONResponse(status_code=500, content=ResponseMessage(message="Database connection error or query failed.", error_code=5001).model_dump())
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        return JSONResponse(status_code=500, content=ResponseMessage(message="An unexpected error occurred.", error_code=5000).model_dump())

@app.get("/api/fairs", response_model=ResponseMessage)
def get_fairs_health_check():
    return ResponseMessage(message="Fair service is healthy")


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)