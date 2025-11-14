#  GCP Assignment — User Upload & Migration

## Overview

This project demonstrates integration between **Google Cloud Datastore** and **BigQuery** using a Java web application.
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

##  How to Run

### 1️ Prerequisites

* Java 11+
* Maven 3+
* GCP Project with:

    * Google App Engine API
    * Datastore API enabled
    * BigQuery dataset created
* Local SDK: Install the Google Cloud SDK (gcloud) on your computer.

### 2️ Build & Run
```bash
Open terminal

1.  C:\Users\projectDir>  maven clean package
2.  C:\Users\projectDir>  gcloud auth application-default login

--> install the tomcat
  copy the project war in target folder and paste in below location :
  
  C:\Users\apache-tomcat-9.0.112\webapps
  
  then go to C:\Users\apache-tomcat-9.0.112\bin
  
  run cmd:       startup.bat

```
Access the app at:
````
[http://localhost:8080]

For Upload user data and authentication
http://localhost:8080/GcpAssignment-1.0-SNAPSHOT/index.html

and for go migration 
http://localhost:8080/GcpAssignment-1.0-SNAPSHOT/migration.html
