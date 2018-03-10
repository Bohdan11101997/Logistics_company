# Logistics_company

## Environment requirements
* Maven 3.3+
* PostgreSQL 10+
* SMTP server

## Setting up development environment
Environment configuration is made using Spring Boot configuration files. List of common configuration properties can be found [here](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)

### Database
All connection parameters (*spring.datasource.\**) are specified in [application.properties](src/main/resources/application.properties) file.

### SMTP server
Mail server parameters (*spring.mail.\**) must be specified in **mail.properties** file, which should be placed in resources folder.
[application.properties](src/main/resources/application.properties) include sample configuration for mail server, which would be overridden by **mail.properties**. 

### Bootstrap
For local usage these files must be placed in *src/main/resources/static/local/* folder.
* "bootstrap.min.css" 3.3.7+
* "bootstrap.min.js" 3.3.7+
* "jquery.min.js" 3.3.1+

## Usage

### Maven
Development environment:
```
mvn spring-boot:run
```
Production environment:
```
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Packaged JAR file
To run JAR file, you must manually specify location for configuration files:
```
java -jar application.jar --spring.config.location=classpath:/application.properties,classpath:/mail.properties
```
