# InstaCV Backend

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![FastAPI](https://img.shields.io/badge/FastAPI-Python-blue.svg)](https://fastapi.tiangolo.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)

## 📋 Overview

InstaCV Backend is an intelligent CV generation and job matching platform that leverages AI/ML technologies to help users create tailored resumes and match them with relevant job opportunities. The system analyzes job descriptions, extracts skills, and provides personalized CV recommendations based on user profiles and job requirements.

## ✨ Key Features

- **AI-Powered CV Generation**: Automatically generate tailored CVs based on job descriptions using advanced AI models
- **CV Parsing**: Upload existing CVs and extract structured information using AI parsing
- **Job Matching**: Intelligent job matching based on skills, experience, and semantic similarity
- **Skill Extraction**: Automatically extract skills and knowledge from job descriptions using NLP models
- **Semantic Similarity**: Match user skills with job requirements using sentence transformers
- **GitHub Integration**: Import projects and repositories from GitHub profiles
- **User Authentication**: Secure authentication with JWT tokens and OAuth2 (Google) support
- **External Job Scraping**: Fetch and analyze remote job postings from external sources
- **Interview Preparation**: Generate interview questions based on job requirements
- **Email Verification**: Secure email verification and password reset functionality
- **Dashboard Analytics**: User dashboard with job statistics and analytics

## 🏗️ Architecture

The application follows a microservices-inspired architecture with two main components:

### 1. **Spring Boot Backend** (Port 8080)
- RESTful API for core business logic
- JWT-based authentication and authorization
- PostgreSQL database integration
- Job management and CV generation
- User profile and GitHub integration
- External job scraping and storage

### 2. **FastAPI AI Service** (Port 7860)
- Skill extraction from job descriptions using JobBERT models
- Semantic similarity matching using sentence transformers
- Machine learning model serving
- Parallel NLP processing

## 🛠️ Technology Stack

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.4.4
- **Language**: Java 21
- **Security**: Spring Security, JWT (jjwt 0.11.5), OAuth2
- **Database**: PostgreSQL (primary), H2 (development)
- **ORM**: Spring Data JPA with Hibernate
- **Validation**: Hibernate Validator, javax.validation
- **Mapping**: MapStruct 1.6.3, ModelMapper 3.2.2
- **Email**: Spring Mail with Thymeleaf templates
- **PDF Processing**: Apache PDFBox 2.0.29
- **HTML Parsing**: JSoup 1.15.3
- **HTTP Client**: OkHttp 4.11.0, Spring WebFlux, OpenFeign
- **AI Integration**: Google Cloud Vertex AI 0.6.0, Groq API
- **Cloud Storage**: Cloudinary
- **Build Tool**: Maven

### AI/ML Service (FastAPI)
- **Framework**: FastAPI
- **Language**: Python 3.9
- **ML Libraries**: 
  - Transformers (Hugging Face)
  - PyTorch
  - Sentence Transformers
  - NumPy
- **Models**:
  - `jjzha/jobbert_skill_extraction` - Skill extraction
  - `jjzha/jobbert_knowledge_extraction` - Knowledge extraction
  - `sentence-transformers/all-MiniLM-L6-v2` - Semantic similarity
- **Web Server**: Uvicorn

### Infrastructure
- **Database**: PostgreSQL
- **Containerization**: Docker & Docker Compose
- **Version Control**: Git

## 📦 Prerequisites

Before running the application, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
- **Maven 3.6+** (included via Maven Wrapper)
- **PostgreSQL 12+** or Docker
- **Python 3.9+** (for FastAPI service)
- **Docker & Docker Compose** (recommended for easy deployment)
- **Git**

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/agmad-team-graduation/InstaCV-Backend.git
cd InstaCV-Backend
```

### 2. Configuration

#### Environment Variables

Create a `.env` file or set the following environment variables:

```bash
# Database Configuration
POSTGRES_DB=postgres
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password

# JWT Configuration
# IMPORTANT: Generate a secure secret key for production
# You can generate one using: openssl rand -base64 32
JWT_SECRET=your_secure_jwt_secret_key

# Gemini API
# Get your API key from: https://ai.google.dev/
GEMINI_API_KEY=your_gemini_api_key

# Cloudinary Configuration
# Get your credentials from: https://cloudinary.com/
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Groq API
# Get your API key from: https://console.groq.com/
GROQ_API_KEY=your_groq_api_key

# Email Configuration (optional)
SPRING_MAIL_USERNAME=your_email
SPRING_MAIL_PASSWORD=your_app_password
```

**⚠️ Security Warning**: Never commit actual API keys or secrets to version control. Use environment variables or a secure secrets management system in production.

#### Application Properties

The application supports multiple profiles:

- `dev` - Local development with local database
- `dev-remote-ai` - Local development with remote AI service (default)
- `dev-remote-ai-db` - Local development with remote AI and database
- `prod` - Production configuration

Set the active profile in `src/main/resources/application.properties`:

```properties
spring.profiles.active=dev-remote-ai
```

### 3. Running with Docker Compose (Recommended)

The easiest way to run the entire stack:

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5433
- FastAPI AI service on port 7860

Then run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

The Spring Boot API will be available at `http://localhost:8080`

### 4. Running Manually

#### A. Start PostgreSQL Database

Using Docker:
```bash
docker run --name postgres_db \
  -e POSTGRES_DB=postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=1234 \
  -p 5433:5432 \
  -d postgres
```

Or use your local PostgreSQL installation.

#### B. Start FastAPI AI Service

```bash
cd fastapi
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 7860
```

The FastAPI service will be available at `http://localhost:7860`

API documentation: `http://localhost:7860/docs`

#### C. Build and Run Spring Boot Application

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/InstaCv-*.jar
```

## 📚 API Documentation

### Main API Endpoints

#### Authentication (`/api/v1/auth`)
- `POST /login` - User login
- `POST /register` - User registration
- `GET /oauth2/authorization/google` - Google OAuth login

#### User Profile (`/api/v1/profile`)
- `GET /` - Get user profile
- `PUT /` - Update user profile
- `DELETE /` - Delete user profile

#### CV Management (`/api/v1/cv`)
- `POST /generate` - Generate tailored CV for a job
- `POST /create` - Create new empty CV
- `POST /create_from_old_cv` - Create CV from uploaded file
- `PUT /{cvId}` - Update CV
- `GET /{cvId}` - Get CV by ID
- `DELETE /{cvId}` - Delete CV

#### Job Management (`/api/v1/jobs`)
- `GET /all` - Get all jobs (paginated)
- `POST /add` - Add new job
- `GET /{jobId}` - Get job details
- `PUT /{jobId}` - Update job
- `DELETE /{jobId}` - Delete job
- `POST /interview-questions` - Generate interview questions

#### External Jobs (`/api/v1/external-jobs`)
- `GET /scrape` - Scrape jobs from external sources
- `GET /all` - Get all external jobs

#### GitHub Integration (`/api/v1/github`)
- `GET /profile` - Get GitHub profile
- `GET /repos` - Get repositories
- `POST /sync` - Sync GitHub data

#### Dashboard (`/api/v1/dashboard`)
- `GET /statistics` - Get user statistics

### FastAPI AI Service Endpoints

#### Skills Extraction (`/skills`)
- `POST /extract-skills` - Extract skills from job description
- `POST /extract-knowledge` - Extract knowledge from text

#### Semantic Similarity (`/similarity`)
- `POST /similarity` - Calculate similarity between two skills
- `POST /match-skills` - Match job skills with user skills

## 📁 Project Structure

```
InstaCV-Backend/
├── src/
│   ├── main/
│   │   ├── java/com/Graduation/InstaCv/
│   │   │   ├── controller/          # REST API controllers
│   │   │   ├── service/              # Business logic services
│   │   │   ├── data/
│   │   │   │   ├── model/            # JPA entities
│   │   │   │   ├── dto/              # Data transfer objects
│   │   │   │   └── repository/       # JPA repositories
│   │   │   ├── security/             # Security configuration
│   │   │   ├── mappers/              # Entity-DTO mappers
│   │   │   ├── gateways/             # External API clients
│   │   │   ├── exceptions/           # Custom exceptions
│   │   │   └── utils/                # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-*.properties
│   └── test/                         # Unit and integration tests
├── fastapi/
│   ├── main.py                       # FastAPI main application
│   ├── skills_extraction/            # Skill extraction module
│   ├── semantic_similarity/          # Similarity matching module
│   ├── requirements.txt              # Python dependencies
│   └── Dockerfile                    # FastAPI Docker image
├── docker-compose.yml                # Docker compose configuration
├── pom.xml                           # Maven configuration
└── README.md                         # This file
```

## 🔧 Development

### Building the Project

```bash
./mvnw clean package
```

### Running Tests

```bash
./mvnw test
```

### Code Quality

The project uses:
- Lombok for reducing boilerplate code
- MapStruct for type-safe bean mapping
- Spring Boot DevTools for hot reload during development

### Database Migrations

The application uses Hibernate's `ddl-auto=update` for automatic schema updates in development. For production, consider using migration tools like Flyway or Liquibase.

## 🐳 Docker Deployment

### Build Docker Images

For the FastAPI service:
```bash
cd fastapi
docker build -t instacv-ai-service .
```

### Production Deployment

For production, update the `docker-compose.yml` with appropriate environment variables and security settings:

```bash
# Make sure to set production environment variables
docker-compose up -d
```

**Production Checklist**:
- Use strong, unique passwords for all services
- Set `spring.profiles.active=prod` in the Spring Boot application
- Configure proper database backups
- Use secure SSL/TLS certificates
- Set up proper logging and monitoring
- Use environment-specific configuration files
- Never expose API keys or secrets in environment variables

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Coding Standards

- Follow Java coding conventions
- Use meaningful variable and method names
- Write comprehensive JavaDoc for public APIs
- Include unit tests for new features
- Keep methods small and focused

## 📝 License

This project is developed as a graduation project by the AGMAD team.

## 👥 Authors

AGMAD Team - Graduation Project

## 🙏 Acknowledgments

- JobBERT models by [jjzha](https://huggingface.co/jjzha)
- Sentence Transformers by [UKPLab](https://www.sbert.net/)
- Spring Boot framework
- FastAPI framework
- Hugging Face for model hosting

## 📞 Support

For issues, questions, or contributions, please open an issue on the GitHub repository.

---

**Note**: This is an active development project. Features and documentation are subject to change.