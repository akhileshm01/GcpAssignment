#  GCP Assignment — User Upload & Migration

## Overview

This project demonstrates integration between **Google Cloud Datastore** and **BigQuery** using a Spring Boot web application.
It allows you to:

1. Upload a user Excel file (`Users.xlsx`) containing user data.
2. Validate and store user details in **Datastore**.
3. Test login functionality using the uploaded credentials.
4. Migrate the stored user data to **BigQuery**.


##  Features
* Upload and parse Excel file (`.xlsx`) containing user records.
* Store records in **Google Cloud Datastore**.
* User authentication (email + password).
* Data migration from **Datastore → BigQuery**.
* Simple and clean web interface with status messages.
* 
##  Tech Stack
| Component      | Technology                  |
| -------------- | --------------------------- |
| Backend        | Spring Boot (Java 11+)      |
| Database       | Google Cloud Datastore      |
| Data Warehouse | Google BigQuery             |
| Frontend       | JSP / HTML Forms            |
| File Format    | Excel (`Users.xlsx`)        |
| Cloud Platform | Google Cloud Platform (GCP) |
---
##  Project Structure
```
GcpAssignment/
│
├── src/
│   ├── main/java/com/example/gcpassignment/
│   │   ├── controller/      # Handles upload & login endpoints
│   │   ├── service/         # Business logic for upload/migration
│   │   ├── model/           # User entity class
│   │   └── util/            # Excel parsing utility
│   │
│   └── resources/
│       ├── application.properties
│       ├── templates/       # JSP pages for Upload & Login
│       └── static/
│
├── Users.xlsx               # Excel file containing 120 user records
├── pom.xml                  # Maven dependencies
└── README.md                # Project documentation
```

## 📊 Excel File Format
Your Excel file should follow this structure:

| Name        | DOB        | Email                                         | Password | Phone    |
| ----------- | ---------- | --------------------------------------------- | -------- | -------- |
| Alice Smith | 15-03-1990 | [alice@example.com](mailto:alice@example.com) | pass123  | 555-1234 |
| Bob Johnson | 22-07-1985 | [bob@example.com](mailto:bob@example.com)     | secret   | 555-5678 |

--> **NOTE:** Use the provided `Users.xlsx` file in the root directory — it already contains 120 user records with valid URL-safe passwords.


##  How to Run

### 1️ Prerequisites

* Java 11+
* Maven 3+
* GCP Project with:

  * Datastore API enabled
  * BigQuery dataset created
* Service Account JSON key (place it under `src/main/resources/`)

### 2️ Build & Run
```bash
mvn clean install
mvn spring-boot:run
```
Access the app at:
 [http://localhost:8080](http://localhost:8080)
---

## 🧪 Usage Steps

###  Step 1: Upload Excel File
1. Open the web page `/upload`.
2. Select and upload `Users.xlsx`.
3. You’ll see a message like:
   ```
   Successfully uploaded 120 users to Datastore.
   ```
###  Step 2: Login Test
1. Enter credentials from the uploaded Excel file:
   ```
   Email: williamsjoel@holmes.info  
   Password: (as per Excel)
   ```
2. On success, you’ll see:
   ```
   Login Successful! Welcome, Mark Parker
   ```
###  Step 3: Migrate Data

1. Click **Start Migration**
2. You’ll get:

   ```
   Migration Successful! Successfully migrated data.
   ```

##  Example Output (from BigQuery)

```json
[
  {
    "email": "acevedotravis@yahoo.com",
    "name": "Deborah Thompson",
    "dob": "06-06-1975",
    "phone": "555-5928"
  },
  {
    "email": "aguilarmatthew@gmail.com",
    "name": "Mr. Darrell Thomas",
    "dob": "01-05-1992",
    "phone": "555-6304"
  }
]
```

##  Security Notes

* Passwords in the Excel file are for demo/testing only.
* For production, store encrypted passwords using Spring Security.
* Always use HTTPS and environment variables for sensitive credentials.

---

You can now copy this entire block into a file named **README.md** inside your `GcpAssignment` folder.
