# 📊 Exhibition Service Swagger UI Documentation

## 🚀 **Swagger UI Configuration**

The Exhibition-service has **Swagger UI fully integrated** and configured. Here's how to access and use it:

### **📍 Access URLs**
- **Swagger UI**: http://localhost:8084/swagger-ui.html
- **API Docs**: http://localhost:8084/v3/api-docs
- **Service Info**: http://localhost:8084/api/exhibitions

## ⚙️ **Configuration Details**

### **Application Properties**
```properties
# Swagger UI Configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.disable-swagger-default-url=true
```

### **Dependencies** (build.gradle)
```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
```

## 🎯 **API Endpoints**

### **📨 Exhibition Management**
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/exhibitions` | Get all exhibitions |
| `GET` | `/api/exhibitions/{id}` | Get exhibition by ID |
| `GET` | `/api/exhibitions/category/{category}` | Get exhibitions by category |
| `GET` | `/api/exhibitions/date/{date}` | Get exhibitions by date |
| `POST` | `/api/exhibitions` | Create new exhibition |
| `PATCH` | `/api/exhibitions/{id}` | Update exhibition |
| `DELETE` | `/api/exhibitions/{id}` | Delete exhibition |
| `PATCH` | `/api/exhibitions/{id}/customers/{customerId}` | Add customer to exhibition |
| `DELETE` | `/api/exhibitions/{id}/customers/{customerId}` | Remove customer from exhibition |

## 📝 **Exhibition Entity Structure**

```json
{
  "id": 1,
  "date": "2025-06-15",
  "category": "Art & Culture",
  "customerIds": [1, 2, 3]
}
```

### **Field Descriptions**
- **id**: Auto-generated unique identifier
- **date**: Exhibition date (LocalDate format: YYYY-MM-DD)
- **category**: Exhibition category (String)
- **customerIds**: Array of customer IDs from customer-service

## 🧪 **Testing with Swagger UI**

### **Step 1: Access Swagger UI**
Navigate to: http://localhost:8084/swagger-ui.html

### **Step 2: Create an Exhibition**
1. **Expand** `POST /api/exhibitions`
2. **Click** "Try it out"
3. **Use example JSON**:
   ```json
   {
     "date": "2025-06-15",
     "category": "Art & Culture",
     "customerIds": [1, 2]
   }
   ```
4. **Click** "Execute"
5. **Note** the returned exhibition ID

### **Step 3: Get All Exhibitions**
1. **Expand** `GET /api/exhibitions`
2. **Click** "Try it out" → "Execute"
3. **View** all exhibitions

### **Step 4: Update Exhibition**
1. **Expand** `PATCH /api/exhibitions/{id}`
2. **Enter** the exhibition ID from step 2
3. **Modify** fields:
   ```json
   {
     "category": "Technology & Innovation",
     "customerIds": [1, 2, 3, 4]
   }
   ```
4. **Click** "Execute"

### **Step 5: Add Customer to Exhibition**
1. **Expand** `PATCH /api/exhibitions/{id}/customers/{customerId}`
2. **Enter** exhibition ID and customer ID
3. **Click** "Execute"

## 🔗 **Integration with Customer Service**

The Exhibition-service references customers by ID from the customer-service:

### **Customer Relationship**
- **Customer Service**: http://localhost:8081/api/customers
- **Exhibition Service**: http://localhost:8084/api/exhibitions
- **Link**: Exhibition stores `customerIds` array referencing customer-service

### **Workflow Example**
1. **Create customers** in customer-service (port 8081)
2. **Note customer IDs** from the response
3. **Create exhibitions** in exhibition-service (port 8084)
4. **Use customer IDs** in the `customerIds` array

## 🏥 **Service Health & Monitoring**

### **Health Check**
The service includes Spring Boot Actuator for health monitoring:
- **Health**: http://localhost:8084/actuator/health
- **Info**: http://localhost:8084/actuator/info

### **Database**
- **PostgreSQL**: `exhibition-db` on port 5433
- **Database**: `exhibitionDB`
- **User**: `exhibition_user`

## 🎨 **Swagger UI Features**

### **Interactive Documentation**
- **Live API testing** directly from browser
- **Request/Response examples**
- **Schema validation**
- **Error handling examples**

### **Organized by Tags**
- **Exhibition Management**: All exhibition-related endpoints
- **Clear descriptions** for each operation
- **Parameter validation** and examples

### **Code Generation**
Swagger UI provides code examples in:
- **cURL** commands
- **JavaScript/Node.js**
- **Python requests**
- **Java**

## 🚀 **Example Usage**

### **Create Exhibition via cURL**
```bash
curl -X POST "http://localhost:8084/api/exhibitions" \
     -H "Content-Type: application/json" \
     -d '{
       "date": "2025-07-01",
       "category": "Science & Technology",
       "customerIds": [1, 2, 3]
     }'
```

### **Get Exhibition by Category**
```bash
curl -X GET "http://localhost:8084/api/exhibitions/category/Art%20%26%20Culture"
```

### **Update Exhibition**
```bash
curl -X PATCH "http://localhost:8084/api/exhibitions/1" \
     -H "Content-Type: application/json" \
     -d '{
       "category": "Modern Art",
       "customerIds": [1, 2, 3, 4, 5]
     }'
```

## 🔧 **Service Architecture**

```
Exhibition-service (Port 8084)
├── Controller (REST API)
├── Service (Business Logic)
├── Repository (Data Access)
├── Model (Exhibition Entity)
├── DTO (Data Transfer Objects)
├── Utils (Mapper)
└── Database (PostgreSQL - exhibition-db:5433)
```

## 📊 **Complete API Documentation**

Access the **interactive Swagger UI** at:
**http://localhost:8084/swagger-ui.html**

All endpoints are documented with:
- ✅ **Request examples**
- ✅ **Response schemas**
- ✅ **Error responses**
- ✅ **Parameter validation**
- ✅ **Live testing capability**

Your Exhibition-service is now **fully functional** with professional API documentation! 