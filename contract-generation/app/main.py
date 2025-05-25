from fastapi import FastAPI, Body, Response
from fastapi.responses import FileResponse
from pydantic import BaseModel, Field
import app.pdf_contract  
import uvicorn
import os
import requests

app = FastAPI(
    title="Contract Generator API",
    description="API for generating company contract PDFs",
    version="1.0.0"
)

class CompanyInfo(BaseModel):
    fair_id: int = Field(..., description="Fair ID")
    square_meters: int = Field(..., description="Number of square meters")
    company_id: str = Field(..., description="Company ID")

class ContractResponse(BaseModel):
    filename: str
    file_path: str
    message: str

@app.post(
    "api/contracts", 
    response_model=ContractResponse,
    summary="Generate a company contract PDF",
    description="Creates a PDF contract based on the provided company information"
)
def create_contract(company_info: CompanyInfo):
    # Generate unique filename based on company name
    # safe_name = "".join(c if c.isalnum() else "_" for c in company_info.company_name)
    # filename = f"contract_{safe_name}.pdf"
    
    #Call fair service to check availability of the fair
    fair_id = company_info.fair_id
    fair_url = "http://localhost:8000/fair"
    
    body = {
        "fair_id": fair_id,
        "square_meters": company_info.square_meters,
        "company_id": company_info.company_id
    }
    
    fair_response = requests.get(fair_url, data=body)
    
    if fair_response.status_code == 200:
       #Call customer service to get customer information
       customer_url = "http://localhost:8000/customer"
       customer_body = {
           "company_id": company_info.company_id
       }
       customer_response = requests.get(customer_url, data=customer_body)

       if customer_response.status_code == 200:
           print("ok")
           #Create contract PDF
           ("Print contract")
           #Create credentials
           
           #Create email
    else:
        message = fair_response.json()["message"]
        
        return ContractResponse(
            filename="",
            file_path="",
            message=message
        )
       
       
    
    print(f"Fair response: {fair_response}")
    
    # Create the PDF contract
    # pdf_contract.create_contract_pdf(
    #     company_info.company_name, 
    #     company_info.registration_id, 
    #     company_info.address,
    #     output_filename=filename
    # )
    
    # file_path = os.path.abspath(filename)
    
    # return ContractResponse(
    #     filename=filename,
    #     file_path=file_path,
    #     message=f"Contract for {company_info.company_name} has been generated successfully"
    # )
    
    return "ok"

@app.get(
    "/contract/{filename}",
    summary="Download a generated contract",
    description="Download a previously generated contract PDF by filename",
    responses={
        200: {
            "content": {"application/pdf": {}},
            "description": "The PDF contract file"
        },
        404: {"description": "Contract not found"}
    }
)

def get_contract(filename: str):
    if not os.path.exists(filename):
        return Response(content=f"Contract {filename} not found", status_code=404)
    return FileResponse(
        path=filename, 
        filename=filename, 
        media_type="application/pdf"
    )

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)