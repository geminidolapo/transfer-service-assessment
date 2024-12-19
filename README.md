# **1. Transfer-Service-Assessment**

A simple Java web application simulating money transfer operations between bank accounts. The application includes REST APIs for processing transactions, retrieving transaction details, and generating daily summaries. It also includes scheduled jobs for commission calculations and daily transaction summaries.

---

## **Features**
- REST APIs for money transfers and transaction management.
- Scheduled jobs for commission calculation and summary generation.
- Support for optional transaction filters (status, account number, date range).
- Transaction fee and commission calculation.

---

# **2. Technology Stack**
- **Framework**: Spring Boot  
- **Language**: Java 11+  
- **Database**: H2  
- **Build Tool**: Maven  
- **Containerization**: Docker/Kubernetes  

---

## **3. Prerequisites**
1. **Java Development Kit (JDK)**: Version 11 or higher.  
2. **Maven**: Version 3.6 or higher.  
3. **Docker (optional)**: For containerized deployment.  
4. **Kubernetes (optional)**: For orchestrating multiple instances.  

---

# **4. Installation Instructions**
### **Clone the Repository**
```bash
git clone https://github.com/geminidolapo/transfer-service-assessment.git
```
#### Clean up the application 
```bash
cd transfer-service-assessment
``` 
```bash
mvn clean install
```
#### To run the application 
```bash
mvn spring-boot:run
```
---

# **5. API Documentation**
## **API Endpoints**

### 1. Process a Transaction
- **Endpoint**: `POST /api/v1/transactions/transfer`
- **Description**: Processes a money transfer request.
- **Request Body**:
```
        { 
            "reference":"test transfer",
            "amount":5.00,
            "currency":"USD",
            "sourceAccountNumber":"1234567890",
            "destinationAccountNumber":"2113182084"
        }
```

### 2. Retrieve Transactions
- **Endpoint**: `GET /api/v1/transactions`
- **Description**: Processes a money transfer request.
- **Query Parameters**:
    status (optional): Transaction status (e.g., SUCCESSFUL).
    sourceAccountNumber (optional): Filter by source account number.
    destinationAccountNumber (optional): Filter destination by account number.
    startDate and endDate (optional): Date range for transactions.

### 3. Daily Summary
- **Endpoint**: `GET /api/v1/transactions/summary`
- **Description**: Processes a money transfer request.
- **Query Parameters**:
    date (optional): The date for which to fetch the summary (default is today).

---
# **6. Running Tests**
#### 1. Unit Tests
Run unit tests with Maven:
```bash
mvn test
```

#### 2. Integration Tests
To test the APIs locally, you can use tools like Postman or cURL.

---
# **7. Dockerization**
### Docker Setup
#### Build the Docker Image
```bash
docker build -t transfer-service-assessment .
```
#### Run the Docker Container
```bash
docker run -p 8080:8080 transfer-service-assessment
```
---

# **8. Deployment in Kubernetes**
Since the app is Kubernetes-ready, YAML manifests have been provided for deployment.

#### Deploy the application using:
```bash
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```
---

## **9. Troubleshooting**
#### Troubleshooting

##### Database Connection Issues
- Double-check the credentials in `application.properties`.

##### Port Conflict
- If port 8080 is already in use, change it in `application.properties`:
```properties
server.port=8081
```
