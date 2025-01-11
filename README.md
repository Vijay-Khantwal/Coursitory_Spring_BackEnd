# **Coursitory Backend** 🚀

Welcome to the **Coursitory Backend** repository! This backend project is built with **Spring Boot** and provides the infrastructure for the **Coursitory** online learning platform. It handles user authentication, course management, and content delivery for a seamless learning experience.

---

## **Project Overview** 📖
This project aims to power an online learning platform, offering key features such as:
- **User Authentication**: Secure user login and registration with JWT-based authentication. 🔐
- **Course Management**: Create, update, and manage courses, including videos and PDFs. 📚
- **Content Delivery**: Stream video content and serve it using GridFS for efficient file handling. 🎥
- **Admin Features**: Admins can manage content, user access, and view course analytics. 🛠️

---

## **Key Features** 🌟

| Feature                  | Description                                           |
| ------------------------ | ----------------------------------------------------- |
| **User Authentication**   | Login/Register functionality with JWT-based auth. 🔑 |
| **Course Management**     | CRUD operations for courses and content. 📘         |
| **Video Streaming**       | Stream course videos with seamless playback. 🎬    |
| **PDF Management**        | Upload and access PDF course materials. 📄          |
| **Admin Dashboard**       | Admin-specific endpoints to manage users and content. 👨‍💼 |

---

## **Tech Stack and Dependencies** 🛠️

| Technology           | Description                                                   |
| -------------------- | ------------------------------------------------------------- |
| **Spring Boot**       | Backend framework used for creating RESTful APIs. 🖥️         |
| **MongoDB**           | Document style database for storing course content and user data. 🗄️  |
| **Spring Security**   | Provides authentication and authorization. 🔒                |
| **JWT**               | Token-based user authentication for secure access. 🔐       |
| **GridFS**            | Used for storing large files like videos and PDFs. 📂        |
| **Lombok**            | Reduces boilerplate code in Java. 💻                          |

---

## **Installation** ⚙️

### **Clone the repository:**

```bash
git clone https://github.com/Vijay-Khantwal/Coursitory_Spring_BackEnd.git
```

### **Navigate to the project directory:**

```bash
cd Coursitory_Spring_BackEnd
```

### **Create an `application.properties` file in `resources`:**

```properties
spring.application.name=spring-coursitory-backend
spring.servlet.multipart.max-file-size=1000MB
spring.servlet.multipart.max-request-size=1000MB
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster-url>/Coursitory?retryWrites=true&w=majority&appName=MyCluster1
spring.data.mongodb.database=Coursitory
jwt.secretKey=<your-secret-key> (**THIS KEY MUST BE ATLEAST OF SIZE 256 BITS Eg:-"d2hhdCBub25zZW5zZSBpcyB0aGlzIGNvZGUgYW5kIGRhdGEgc3RydWN0dXJlcw=="**)
MAX_PDF_SIZE=<SIZE_IN_BYTES>
admin.credentials.username=<YOUR_ADMIN_USERNAME>
admin.credentials.password=<YOUR_ADMIN_PASSWORD>
```

### **Build and run the project:**

```bash
./mvnw clean install
./mvnw spring-boot:run
```

---

## **Key Components** 🔑

| Component              | Description                                         |
| ---------------------- | --------------------------------------------------- |
| **JwtFilter**           | Validates JWT tokens for secure access. 🔑         |
| **SecurityConfig**      | Configures security settings for the application. 🔐|
| **AdminController**     | Manages admin-specific operations and content. 🛠️  |
| **CourseController**    | Handles course-related operations and endpoints. 📚 |
| **VideoController**     | Handles video streaming and metadata retrieval. 🎥 |
| **PDFController**       | Manages PDF upload and retrieval. 📄               |
| **UserController**      | Manages user authentication and enrollment. 👤    |

---

## **Environment Variables** 🔒

| Variable                        | Description                                     |
| ------------------------------- | ----------------------------------------------- |
| **spring.data.mongodb.uri**      | MongoDB connection URI.                         |
| **spring.data.mongodb.database** | MongoDB database name.                          |
| **MAX_PDF_SIZE**                 | Maximum allowed size for PDF uploads.          |
| **jwt.secretKey**                | Secret key for JWT token generation. 🔑        |
| **admin.credentials.username**   | Admin username. 👨‍💼                             |
| **admin.credentials.password**   | Admin password. 🔒                              |

---

## **Contributing** 🤝

1. Fork the repository.
2. Create your feature branch:

```bash
git checkout -b feature/AmazingFeature
```

3. Commit your changes:

```bash
git commit -m 'Add some AmazingFeature'
```

4. Push to the branch:

```bash
git push origin feature/AmazingFeature
```

5. Open a pull request.
