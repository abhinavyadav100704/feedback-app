# 📢 Feedback App (Backend)

A backend application built with **Spring Boot (Java 21)** to manage users and their feedback submissions.  
This project demonstrates secure **user authentication** with JWT, feedback management APIs, and interaction with relational databases.  

I built this project to strengthen my backend skills in **Java, Spring Boot, and database design**, while solving a real use case: allowing users to register, authenticate, and submit feedback easily.

---

## 🚀 Features

- **User Management**
  - Register new users
  - User authentication with JWT
  - Role-based access control (`ROLE_ADMIN` can fetch all users)

- **Feedback Management**
  - Submit feedback
  - Retrieve all feedback entries
  - Retrieve feedback by ID

- **Security**
  - JWT-based authentication
  - Spring Security integration
  - Protected endpoints (e.g., `/users` only accessible by admin)

- **Persistence**
  - Relational database design with `Users` and `Feedbacks` tables
  - ORM with Hibernate JPA and MySQL
  - In-memory **H2 database** for quick testing

---

## 🛠 Technologies Used

- **Backend:** Spring Boot, Java 21  
- **Database:** MySQL, Hibernate (JPA), H2 Database (for testing)  
- **Security:** Spring Security, JWT  
- **Build Tool:** Maven  
- **Testing:** Postman (API testing)  
- **Utilities:** Lombok  

---

## ⚙️ Installation & Setup

### Prerequisites
- Install **Java 21**
- Install **Maven**
- Install **MySQL** and create a database (`feedbackdb`)

### Steps
1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/feedback-app.git
   cd feedback-app
   
2.spring.datasource.url=jdbc:mysql://localhost:3306/feedbackdb
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

3.mvn spring-boot:run

4.http://localhost:8080

▶️ API Endpoints : 
🔹 Authentication & Users (UserController) : 
Register User
POST /api/auth/register
Request body:
{
  "username": "abhinav",
  "password": "secure123",
  "email": "abhinav@example.com"
}
Login
POST /api/auth/login
Response:
{
  "username": "abhinav",
  "token": "eyJhbGciOiJIUzI1NiIsInR..."
}
Get All Users (Admin only)
GET /api/users
Requires valid JWT token and ROLE_ADMIN.

🔹 Feedback (FeedbackController) : 
Create Feedback
POST /api/feedback
{
  "message": "This is my feedback",
  "userId": 1
}
Get All Feedback
GET /api/feedback

Get Feedback by ID
GET /api/feedback/{id}

🧪 Testing :  
Tested all endpoints using Postman.

Verified authentication flow: register → login → receive JWT → access protected routes.

Used H2 in-memory DB for unit tests and MySQL for persistent storage.

📌 Project Status : 
This is a personal project to practice backend development with Spring Boot.
Future improvements may include:

Adding a frontend client (React/Angular)
Pagination and filtering for feedbacks
Admin dashboard

🤝 Contribution : 
This is a personal project, so contributions are not open at the moment.
