# Spring Boot JWT Authentication

A Spring Boot 3.5.7 example demonstrating JWT authentication with Spring Security & Spring Data JPA.

## Tech Stack

| Technology | Version |
|------------|---------|
| Spring Boot | 3.5.7 |
| Spring Security | 6.x |
| Java | 21 |
| JJWT | 0.12.6 |

## Features

- JWT token-based authentication
- Spring Security integration
- Spring Data JPA for persistence
- H2 Database (default) or MySQL support
- Role-based access control (ROLE_USER, ROLE_MODERATOR, ROLE_ADMIN)

For more detail, please visit:
> [Spring Boot JWT Authentication](https://github.com/rslakra/Spring.git)

– MySQL
```xml
<!-- https://mvnrepository.com/artifact/com.mysql/mysql-connector-j -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.2.0</version>
    <scope>runtime</scope>
</dependency>
```
## DataSource Configuration
Open `src/main/resources/application.properties`

- MySQL
```
spring.datasource.url= jdbc:mysql://localhost:3306/testdb?useSSL=false
spring.datasource.username= root
spring.datasource.password= 123456

spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto= update

# App Properties
app.jwtSecret= rlakra
app.jwtExpirationMs= 86400000
```

## Run Spring Boot application

```
mvn spring-boot:run
```

## Run following SQL insert statements
```
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```


## Author
- Rohtash Lakra

