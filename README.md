# Web-Library 2.0

### **Brief description of the project**

This is my second full-fledged project using the Spring framework. **[Here's first project link](https://github.com/RustamAbdusamatov4848/LibraryWithJDBCTemplate)**. 
The essence of this project is to provide simple functionality for an online library, where a conditional librarian has 2 tables (a list of people and a list of books). 
The librarian and the person who borrows books from the library have different access to library data.

## Getting Started

## Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java 21**: Make sure Java Development Kit (JDK) 21 is installed on your machine. You can download it from [Oracle's official website](https://www.oracle.com).
- **Docker**: Install Docker to run the application and manage containers. You can download Docker from [Docker's official website](https://www.docker.com/get-started).
- **Docker Compose**: Ensure you have Docker Compose installed, which is typically included with Docker installations.

### Clone the Repository 

First, clone the repository to your local machine: 

```
bash git clone https://github.com/yourusername/library-with-security.git cd library-with-security 
``` 

### Configuration 

1. **Database Configuration**: 

Update the database settings in the `application.properties` file if necessary. The default settings are configured for a PostgreSQL database running in a Docker container. 

```
spring.datasource.url=jdbc:postgresql://db:5432/library 
spring.datasource.username=your_username 
spring.datasource.password=your_password 
``` 
2. **Flyway Migrations**: Ensure your SQL migration files are placed in the `src/main/resources/db/migration` directory. 

### Running the Application 

To start the application, use Docker Compose. This will build the necessary containers and run your application along with the PostgreSQL database and Flyway for database migrations: 

```
bash docker-compose up 
``` 

### Migrating the Database Flyway will automatically run the migrations when the application starts. If you need to manually trigger a migration or repair the schema history, you can do so by running: 

```
bash docker-compose run --rm flyway migrate docker-compose run --rm flyway repair 
``` 

### Accessing the Application 

Once the application is up and running, you can access it at `http://localhost:8080`. You can test the API endpoints or interact with the web interface (if implemented). 

### Stopping the Application 

To stop the application and remove the containers, you can run: ```bash docker-compose down ``` 


You are now ready to start using Web-Library!

## Note
The UI uses Englush language

## Technologies and Libraries

The Web-Library project uses the following technologies and libraries:

- **Spring Boot**: Provides a framework for building stand-alone, production-grade Spring applications.
  - `spring-boot-starter-data-jpa`: Starter for using Spring Data JPA with Hibernate.
  - `spring-boot-starter-security`: Starter for using Spring Security.
  - `spring-boot-starter-thymeleaf`: Starter for using Thymeleaf as the view layer.
  - `spring-boot-starter-validation`: Starter for using Java Bean Validation with Hibernate Validator.
  - `spring-boot-starter-web`: Starter for building web, including RESTful, applications using Spring MVC.
- **Thymeleaf**: A modern server-side Java template engine for web and standalone environments.
  - `thymeleaf-extras-springsecurity6`: Integrates Spring Security with Thymeleaf.
- **MySQL**: A popular relational database management system.
  - `mysql-connector-j`: JDBC driver for MySQL.
- **Lombok**: A library that helps reduce boilerplate code in Java.
  - `lombok`: Annotation library for generating boilerplate code.
- **Testing Frameworks**:
  - `spring-boot-starter-test`: Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest, and Mockito.
  - `spring-security-test`: Support for testing Spring Security applications.


## Usage
The librarian has the ability to:

1. Librarian authorization and registration
2. View reader list
3. Add/Edit and remove reader from the database;
4. View book list;
5. Add / Edit and delete books from the database;
6. Give out books to a certain reader, or after the reader returned the book back to the library, mark it in the database.
7. Search books. I plan to add a reader search function in the future.
