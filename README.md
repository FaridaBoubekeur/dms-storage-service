# 📦 DMS Storage Microservice

This is the **Storage Microservice** for the Document Management System (DMS) project.  
It handles secure **file uploads and downloads** via **MinIO** (S3-compatible) using **Spring Boot**.

---

## 🚀 Features

- Upload files to MinIO using REST API
- Generate secure pre-signed download URLs
- Store files with UUID-based keys per document
- Ready for integration with Document Microservice

---

## 🧰 Tech Stack

- Java 17 + Spring Boot
- AWS SDK v2 (S3 + Presigner)
- MinIO (Docker)
- Postman for testing

---

## 🛠️ API Endpoints

### 🔼 Upload File
`POST /api/files/upload/{documentId}`  
**Body:** `multipart/form-data` with field `file`  
**Returns:** Key of uploaded file

### 🔽 Get Pre-signed Download URL
`GET /api/files/download-url?key=your_s3_key`  
**Returns:** Temporary download URL

---

## 📦 MinIO Setup

Start MinIO locally:

bash:
`docker run -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=admin \
  -e MINIO_ROOT_PASSWORD=password \
  quay.io/minio/minio server /data --console-address ":9001"`


Then access `http://localhost:9001`
**Bucket Name**: documents
**Credentials**:

Access Key: `admin`
Secret Key: `password`

---

### 📌 To Do (Integration Phase)

- Link uploads to Document MS
- Enforce access control via department/user role
- Unit & integration testing
- Swagger/OpenAPI documentation

---

**ENSIA - Enterprise Computing Project
📅 May 2025** 

