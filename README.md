# **1. transfer-service-assessment
A simple Java web application simulating money transfer operations between bank accounts.  The application includes REST APIs for processing transactions, retrieving transaction details,  and generating daily summaries. It also includes scheduled jobs for commission calculations  and daily transaction summaries.

## Features
- REST APIs for money transfers and transaction management.
- Scheduled jobs for commission calculation and summary generation.
- Support for optional transaction filters (status, account number, date range).
- Transaction fee and commission calculation.

## **2. Technology Stack
- **Framework**: Spring Boot
- **Language**: Java 11+
- **Database**: H2
- **Build Tool**: Maven
- **Containerization**: Docker/Kubernetes

## Prerequisites
1. **Java Development Kit (JDK)**: Version 11 or higher.
2. **Maven**: Version 3.6 or higher.
3. **Docker (optional)**: For containerized deployment.
4. **Kubernetes (optional)**: For orchestrating multiple instances.

## **3. Installation Instructions

### Clone the Repository
git clone https://github.com/your-repository-url/bank-transfer-app.git
cd bank-transfer-app

###
mvn clean install

### 
mvn spring-boot:run

### **4. API Documentation**

## API Endpoints

### 1. Process a Transaction
- **Endpoint**: `POST /api/v1/transactions/transfer`
- **Description**: Processes a money transfer request.
- **Request Body**:
{
    "reference":"test transfer",
    "amount":5.00,
    "currency":"USD",
    "sourceAccountNumber":"1234567890",
    "destinationAccountNumber":"2113182084"
}

### 2. Retrieve Transactions
- **Endpoint**: `POST GET /api/v1/transactions`
- **Description**: Processes a money transfer request.
- **Query Parameters**:
    status (optional): Transaction status (e.g., SUCCESSFUL).
    sourceAccountNumber (optional): Filter by source account number.
    destinationAccountNumber (optional): Filter destination by account number.
    startDate and endDate (optional): Date range for transactions.

### 3. Daily Summary
- **Endpoint**: `POST GET /api/v1/transactions/summary`
- **Description**: Processes a money transfer request.
- **Query Parameters**:
    date (optional): The date for which to fetch the summary (default is today).


### **5. Running Tests**
## Unit Tests
Run unit tests with Maven:
mvn test

## Integration Tests
To test the APIs locally, you can use tools like Postman or cURL.

### **6. Dockerization**
## Docker Setup
### Build the Docker Image
docker build -t transfer-service-assessment .
### Run the Docker Container
docker run -p 8080:8080 transfer-service-assessment

### **7. Deployment in Kubernetes**
If the app is Kubernetes-ready, provide YAML manifests for deployment:
```markdown
## Kubernetes Deployment

### Deploy the application using:
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

### **8. Troubleshooting**
## Troubleshooting

### Database Connection Issues
- Double-check the credentials in `application.properties`.

### Port Conflict
- If port 8080 is already in use, change it in `application.properties`:
```properties
server.port=8081


