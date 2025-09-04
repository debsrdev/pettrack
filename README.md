# 🐾 PetTrack

PetTrack is a backend API built with **Java 21** and **Spring Boot** to manage pets and their veterinary medical records.  
The project provides secure role-based access, CRUD operations for pets and medical records, and is designed to support both pet owners and veterinarians.

---

## 🚀 Features

### ✅ Implemented
- **Authentication & Authorization**
    - JWT-based authentication.
    - Role management (`USER`, `VETERINARY`).
    - Login with either `username` or `email`.

- **User Management**
    - User entity with roles.
    - Owners can have multiple pets.

- **Pet Management (CRUD)**
    - `GET /api/pets` → Get all pets.
    - `GET /api/pets/{id}` → Get pet by ID.
    - `POST /api/pets` → Create new pet *(only veterinarians)*.
    - `PUT /api/pets/{id}` → Update pet *(only veterinarians)*.
    - `DELETE /api/pets/{id}` → Delete pet *(only veterinarians)*.
    - Filtering pets by species, breed, etc.
    - Exception handling with custom `EntityNotFoundException` and `GlobalExceptionHandler`.

### 🐶 Pet Endpoints

#### Create a Pet (Veterinarian only)
**POST** `/api/pets`

Request:
```json
{
  "name": "Luna",
  "species": "Dog",
  "breed": "Labrador Retriever",
  "birthDate": "2022-05-10",
  "image": "https://example.com/luna.jpg",
  "username": "john_doe"
}
```

#### Update a Pet (Veterinarian only)
**PUT** `/api/pets/5`

Request:
```json
{
  "name": "Luna",
  "species": "Dog",
  "breed": "Golden Retriever",
  "birthDate": "2022-05-10",
  "image": "https://example.com/luna_updated.jpg",
  "username": "john_doe"
}
```

- **Medical Records (CRUD)**
    - `GET /api/medical-records` → Get all medical records.
    - `GET /api/medical-records/{id}` → Get medical record by ID.
    - `POST /api/medical-records` → Create new medical record *(only veterinarians)*.
    - `PUT /api/medical-records/{id}` → Update medical record *(only veterinarians)*.
    - `DELETE /api/medical-records/{id}` → Delete medical record *(only veterinarians)*.

### 🐶 MedicalRecord Endpoints

#### Create a MedicalRecord (Veterinarian only)
**POST** `/api/medical-records`

Request:
```json
{
  "description": "Annual check-up and vaccination",
  "weight": "21.8",
  "date": "2025-09-01",
  "type": "CHECKUP",
  "petId": 5,
  "createdBy": "dr_smith"
}
```

#### Update a MedicalRecord (Veterinarian only)
**PUT** `/api/medical-records/5`

Request:
```json
{
  "description": "Rabies vaccination booster",
  "weight": "22.5",
  "date": "2025-08-15",
  "type": "VACCINATION",
  "petId": 5,
  "createdBy": "dr_smith"
}

```

- **Testing**
  - Unit tests with **JUnit** and **Mockito**.
  - Integration tests with **MockMvc** and `@Sql` dataset.
  - Coverage > 70% (branches).

- **API Documentation**
  - Full **Swagger** / **OpenAPI** documentation at `/swagger-ui/index.html`.
  - Includes example requests/responses, role-based access, and error codes.

---

## 🧪 Test Data

Sample users in `test-data.sql` for testing endpoints:

```sql
INSERT INTO users (id, username, email, password, role) VALUES
(1, 'dr_smith', 'smith@clinic.com', 'encrypted_password', 'VETERINARY'),
(2, 'john_doe', 'john@example.com', 'encrypted_password', 'USER');
```

Sample pets and medical records also included for integration tests.

---

## 🛠️ Tech Stack

- **Backend:** Java 21, Spring Boot 3
- **Database:** MySQL
- **ORM:** Hibernate / JPA
- **Security:** Spring Security, JWT
- **Build Tool:** Maven
- **Testing:** JUnit 5, Mockito, MockMvc
- **Containerization:** Docker
- **API Docs:** Springdoc OpenAPI / Swagger UI

---

## 📂 Project Structure

```
src/main/java/com/femcoders/pettrack
├── controllers      # REST endpoints (Auth, User, Pet, MedicalRecord)
├── dtos             # DTOs for requests and responses
├── exceptions       # Custom exceptions & error handling
├── models           # Entity classes (User, Pet, MedicalRecord)
├── repositories     # Spring Data JPA repositories
├── security         # JWT filters, UserDetails, config
├── services         # Business logic layer
└── utils            # Role Validator class
```


---

## 🔒 Security Rules

- **Pet CRUD**
  - `USER`: Read-only.
  - `VETERINARY`: Full access (create, update, delete).
- **Medical Records**
  - Managed only by **VETERINARY** role.
- **User Management**
  - Restricted to **VETERINARY** role.

---

## 📦 How to Run the Project

1. **Clone the repository:**
```bash
git clone https://github.com/your-username/pettrack.git
cd pettrack
```

2. **Start MySQL (Docker example):**
```bash
docker run --name mysql-pettrack -e MYSQL_ROOT_PASSWORD=root123456 -e MYSQL_DATABASE=pettrack -p 3306:3306 -d mysql:8.0
```

3. **Run the application:**
```bash
./mvnw spring-boot:run
```

4. **Access Swagger UI:**
```
http://localhost:8080/swagger-ui/index.html
```

---

## 👩‍💻 Author

Developed by **Débora Rubio** as part of an individual backend development project.
