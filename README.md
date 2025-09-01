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

---

## 🛠️ Tech Stack

- **Backend:** Java 21, Spring Boot 3
- **Database:** MySQL
- **ORM:** Hibernate / JPA
- **Security:** Spring Security, JWT
- **Build Tool:** Maven
- **Testing:** JUnit 5, Mockito, MockMvc
- **Containerization:** Docker

---

## 📂 Project Structure

src/main/java/com/femcoders/pettrack
- controllers → REST controllers (Auth, Pet, MedicalRecord)
- dtos → Data Transfer Objects (e.g., PetRequest, CartDTO)
- exceptions → Custom exceptions and global handler
- models → Entities (User, Pet, MedicalRecord)
- repositories → Spring Data JPA repositories
- security → JWT authentication & security config
- services → Service layer interfaces & implementations


---

## 🔒 Security Rules

- **Pet CRUD**
    - `USER`: Read-only.
    - `VETERINARY`: Full access (create, update, delete).
- **Medical Records**
    - Managed only by **VETERINARY** role.

---

## 👩‍💻 Author

Developed by **Débora Rubio** as part of an individual backend development project.
