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
5. Add / Edit and delete books from the database;
6. Give out books to a certain reader, or after the reader returned the book back to the library, mark it in the database.
7. Search books. I plan to add a reader search function in the future.

---

# Web-Library 2.0

### **Краткое описание проекта**

Это мой второй полноценный проект с использованием фреймворка Spring. **[Вот ссылка на первый проект](https://github.com/RustamAbdusamatov4848/LibraryWithJDBCTemplate)**.
Суть этого проекта заключается в предоставлении простой функциональности для онлайн-библиотеки, где условный библиотекарь имеет 2 таблицы (список людей и список книг).
Библиотекарь и человек, который берет книги в библиотеке, имеют различный доступ к данным библиотеки.

## Установка проекта

## Установка

Для установки и запуска проекта Web-Library версии 2.0 выполните следующие шаги:

1. Клонируйте репозиторий на ваш локальный компьютер:

    ```bash
    git clone https://github.com/RustamAbdusamatov4848/LibraryWithSecurity.git
    ```

2. Перейдите в директорию проекта:

    ```bash
    cd LibraryWithSecurity
    ```

3. Установите зависимости с помощью Maven:

    ```bash
    mvn install
    ```

4. Запустите приложение:
   
    Для запуска приложения вам понадобится Tomcat (я использую версию 10).

5. Откройте браузер и перейдите по следующему адресу:

    ```
    http://localhost:8080/index.html
    ```

Теперь вы готовы начать использовать Web-Library!

## Примечание
Интерфейс использует английский язык.

## Технологии и библиотеки

Проект Web-Library использует следующие технологии и библиотеки:

- **Spring Boot**: Фреймворк для создания автономных, готовых к использованию Spring-приложений.
  - `spring-boot-starter-data-jpa`: Стартер для использования Spring Data JPA с Hibernate.
  - `spring-boot-starter-security`: Стартер для использования Spring Security.
  - `spring-boot-starter-thymeleaf`: Стартер для использования Thymeleaf в качестве уровня представления.
  - `spring-boot-starter-validation`: Стартер для использования Java Bean Validation с Hibernate Validator.
  - `spring-boot-starter-web`: Стартер для создания веб-приложений, включая RESTful приложения, с использованием Spring MVC.
- **Thymeleaf**: Современный серверный Java-шаблонизатор для веб-среды и автономных приложений.
  - `thymeleaf-extras-springsecurity6`: Интеграция Spring Security с Thymeleaf.
- **MySQL**: Популярная система управления реляционными базами данных.
  - `mysql-connector-j`: JDBC-драйвер для MySQL.
- **Lombok**: Библиотека, помогающая уменьшить шаблонный код в Java.
  - `lombok`: Библиотека аннотаций для генерации шаблонного кода.
- **Фреймворки для тестирования**:
  - `spring-boot-starter-test`: Стартер для тестирования Spring Boot приложений с библиотеками, включая JUnit, Hamcrest и Mockito.
  - `spring-security-test`: Поддержка тестирования приложений Spring Security.

## Использование

Библиотекарь имеет возможность:

1. Авторизация и регистрация библиотекаря
2. Просмотр списка читателей
3. Добавление/редактирование и удаление читателей из базы данных;
4. Просмотр списка книг;
5. Добавление/редактирование и удаление книг из базы данных;
6. Выдача книг определенному читателю, или после того, как читатель вернул книгу обратно в библиотеку, отметка этого в базе данных.
7. Поиск книг. В будущем планирую добавить функцию поиска читателей.

