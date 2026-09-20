# Project NOVA Membership Backend

Spring Boot + Java 17 + MySQL backend for the NOVA membership form.

## Form fields supported

- Full name
- Date of birth
- Mobile number
- Email
- City
- School / College
- Class
- Stream
- Board
- Areas of interest
- Password
- Gender
- District (Maharashtra)
- Profile photo
- Terms acceptance

## 1. Requirements

Install:
- JDK 17+
- Maven 3.9+
- MySQL 8+

Check:
```powershell
java -version
mvn -version
mysql --version
```

## 2. Database

Open MySQL:
```sql
CREATE DATABASE nova_db;
```

The application also has `createDatabaseIfNotExist=true`, but creating it manually is recommended.

Default local credentials in `application.properties`:
- username: root
- password: root

If your MySQL password is different, either edit the properties or set environment variables.

PowerShell example:
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```

## 3. Run

From this project folder:
```powershell
mvn clean spring-boot:run
```

Backend:
```text
http://localhost:8080
```

Health:
```text
GET http://localhost:8080/api/health
```

## 4. Register API

Endpoint:
```text
POST http://localhost:8080/api/members/register
Content-Type: multipart/form-data
```

Multipart keys:
```text
fullName
dateOfBirth       -> yyyy-MM-dd
mobileNumber
email
city
schoolCollege
className
stream
board
areasOfInterest
password
gender
district
termsAccepted     -> true
profilePhoto      -> image file
```

Example frontend:
```javascript
const formData = new FormData();

formData.append("fullName", fullName);
formData.append("dateOfBirth", dateOfBirth); // yyyy-MM-dd
formData.append("mobileNumber", mobileNumber);
formData.append("email", email);
formData.append("city", city);
formData.append("schoolCollege", schoolCollege);
formData.append("className", className);
formData.append("stream", stream);
formData.append("board", board);
formData.append("areasOfInterest", areasOfInterest);
formData.append("password", password);
formData.append("gender", gender);
formData.append("district", district);
formData.append("termsAccepted", termsAccepted);
formData.append("profilePhoto", photoFile);

const response = await fetch("http://localhost:8080/api/members/register", {
  method: "POST",
  body: formData
});

const data = await response.json();
console.log(data);
```

Do NOT manually set `Content-Type` when sending FormData from the browser.

## 5. Login API

```text
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

Body:
```json
{
  "email": "member@example.com",
  "password": "your-password"
}
```

Response contains:
- JWT token
- member profile

Use it for protected APIs:
```text
Authorization: Bearer YOUR_TOKEN
```

Authenticated profile:
```text
GET http://localhost:8080/api/auth/me
```

## 6. Profile photo

Accepted:
- JPG
- JPEG
- PNG
- WEBP

Maximum:
- 5 MB

Files are stored in:
```text
uploads/profile-photos/
```

Returned URL example:
```text
http://localhost:8080/uploads/profile-photos/filename.webp
```

## 7. Important production changes

Before deployment:
1. Change `DB_USERNAME` and `DB_PASSWORD`.
2. Set a strong `JWT_SECRET` of 32+ bytes.
3. Move profile photos to cloud/object storage if the server is ephemeral.
4. Restrict CORS to the real frontend domain.
5. Use HTTPS.
6. Change `spring.jpa.hibernate.ddl-auto` from `update` to a migration strategy such as Flyway.

## 8. API flow

Registration:
```text
React form
   ↓
POST /api/members/register
   ↓
Validation
   ↓
Check duplicate email/mobile
   ↓
BCrypt password hashing
   ↓
Profile photo storage
   ↓
MySQL members table
   ↓
Success response
```

Login:
```text
React login
   ↓
POST /api/auth/login
   ↓
Check email + BCrypt password
   ↓
JWT token
   ↓
Frontend stores token
   ↓
Bearer token for protected requests
```
