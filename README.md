# Web-Library 2.0

### **Brief description of the project**

The essence of this project is to provide simple functionality for an online library, where a conditional librarian has
2 tables (a list of people and a list of books). *
*[Here is the project this project is based on](https://github.com/RustamAbdusamatov4848/LibraryWithJDBCTemplate)**

## Getting Started

## Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java 21**: Make sure Java Development Kit (JDK) 21 is installed on your machine. You can download it
  from [Oracle's official website](https://www.oracle.com).
- **Docker**: Install Docker to run the application and manage containers. You can download Docker
  from [Docker's official website](https://www.docker.com/get-started).
- **Docker Compose**: Ensure you have Docker Compose installed, which is typically included with Docker installations.
- **PostgreSQL**: create database with name 'library' / or skip it, if DB will be launched in container
- **Postman**: If necessary, install Postman to interact with the application.

### Clone the Repository

First, clone the repository to your local machine:

```
bash git clone https://github.com/yourusername/library-with-security.git cd library-with-security 
``` 

### Configuration

1. #### **Database Configuration**:

Update the database settings in the `application.properties` file if necessary. The default settings are configured for
a PostgreSQL database running in a Docker container.

```
spring.datasource.url=jdbc:postgresql://db:5432/library
```

or if you're going to start only on your local machine

```
spring.datasource.url=jdbc:postgresql://localhost:5432/library
```

```
spring.datasource.username=your_username 
spring.datasource.password=your_password 
```

2. #### **Flyway Migrations**:

Ensure your SQL migration files are placed in the `src/main/resources/db/migration` directory.

### Running the Application

To start the application, use Docker Compose. This will build the necessary containers and run your application along
with the PostgreSQL database and Flyway for database migrations:

```
bash docker-compose up 
``` 

### Accessing the Application

Once the application is up and running, you can access it at `http://localhost:8081`.
You can test the API endpoints or interact with the web interface.

## Note

The UI uses English

## Usage

Congratulates, you are now ready to start using Web-Library!
You can interact with application through Postman or Swagger.

The librarian has the ability to:

1. View reader list
2. Add/Edit and remove reader from the database;
3. View book list;
4. Add / Edit and delete books from the database;
5. Give out books to a certain reader, or after the reader returned the book back to the library, mark it in the
   database.
6. Search books. I plan to add a reader search function in the future.

## Load Testing

To perform load testing follow the steps below:

1. Open Postman
2. Import json from `src/main/resources/library-postman/postman_collection.json` to Postman
3. Run imported collection
