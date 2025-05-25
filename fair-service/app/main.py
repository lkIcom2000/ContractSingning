import fastapi
import uvicorn
from pydantic import BaseModel
from fastapi.responses import JSONResponse


app = fastapi.FastAPI()


class FairData(BaseModel):
    fair_id: int
    square_meters: int
    hall_id: int
    # Add other data fields here, for example:
    # name: str
    # description: str | None = None

class ResponseMessage(BaseModel):
    message: str
    error_code: int | None = None

@app.post("/fair", response_model=ResponseMessage)
def process_fair_data(data: FairData):
    print(f"Received data: {data}")
    
    if data.fair_id == 1: #
        #Hall B is full
        if data.hall_id == 2:
            return JSONResponse(status_code=400, content=ResponseMessage(message="Hall B is has no available space").model_dump())
        elif data.hall_id == 1:
            #Hall A is available but check whether there is enough space
            if data.square_meters > 45:
                return JSONResponse(status_code=400, content=ResponseMessage(message="Hall A is available but there is no enough space").model_dump())
            else:
                return ResponseMessage(message="Hall A is available")
            
        elif data.hall_id == 3:
            #Hall C is available but check whether there is enough space
            if data.square_meters > 100:
                return JSONResponse(status_code=400, content=ResponseMessage(message="Hall C is available but there is no enough space").model_dump())
            else:
                return ResponseMessage(message="Hall C is available")
    else:
        return JSONResponse(status_code=404, content=ResponseMessage(message="Fair is not available").model_dump())

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)