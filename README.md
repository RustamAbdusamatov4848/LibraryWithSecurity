# Web-Library 2.0

### **Brief description of the project**

This is my second full-fledged project using the Spring framework. **[Here's first project link](https://github.com/RustamAbdusamatov4848/LibraryWithJDBCTemplate)**. 
The essence of this project is to provide simple functionality for an online library, where a conditional librarian has 2 tables (a list of people and a list of books). 
The librarian and the person who borrows books from the library have different access to library data.

## Install project

## Installation

To install and run the Web-Library project v. 2.0, follow these steps:

1. Clone the repository on your local machine:

    ```bash
    git clone https://github.com/RustamAbdusamatov4848/LibraryWithSecurity.git
    ```

2. Navigate to the project directory:

    ```bash
    cd LibraryWithSecurity
    ```

3. Install dependencies using Maven:

    ```bash
    mvn install
    ```

4. Run the application:
   
    To run the application you will need Tomcat (I'm using version 10)

5. Open your browser and go to the following URL:

    ```
    http://localhost:8080/index.html
    ```

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
2. Add / Edit and delete books from the database;
4. Give out books to a certain reader, or after the reader returned the book back to the library, mark it in the database.
5. Search books. I plan to add a reader search function in the future.



