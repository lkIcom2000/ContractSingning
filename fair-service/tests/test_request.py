import requests
import json


data = {
    "fair_id": 1,
    "square_meters": 100,
    "hall_id": 1
}

headers = { "Content-Type": "application/json" }
 
print("Testing fair service")

try:
    response = requests.post("http://localhost:8000/api/fairs", json=data, headers=headers)
    print("Fair service Status Code: ", response.status_code)
    print("Fair service Headers: ", response.headers)
    print("Fair service Body: ", response.text)
except Exception as e:
    print(f"Error with fair service: {e}")