import requests
import json


data = {
    "fair_id": 1,
    "square_meters": 100,
    "company_id": "1234567890"
}

headers = { "Content-Type": "application/json" }

print("Testing contract generation service")

try:
    response = requests.post("http://localhost:8000/api/contracts", json=data, headers=headers)
    print("Contract generation service Status Code: ", response.status_code)
    print("Contract generation service Headers: ", response.headers)
    print("Contract generation service Body: ", response.text)
    
except Exception as e:
    print(f"Error with contract generation service: {e}") 