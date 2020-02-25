Mokka
---
[![Build Status](https://travis-ci.com/hycomsa/mokka.svg?branch=master)](https://travis-ci.com/hycomsa/mokka)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=pl.hycom.mokka%3Amokka-parent&metric=alert_status)](https://sonarcloud.io/dashboard?id=pl.hycom.mokka%3Amokka-parent)

## Prerequisities
Database of your choice: Oracle, MySQL, HSQLDB, H2, PostgreSQL

## Install
### Precompiled binaries
You may use precompiled binary for release version available on [GithHub Releases page](https://github.com/hycomsa/mokka/releases).
### Building from sources
Or build the application directly from sources:
```
cd src
mvn clean package
```
## Configuration
The minimal configuration is to provide datasource details:
```
spring.datasource.url=
spring.datasource.username = 
spring.datasource.password = 
```
in `application.properties` file which should be placed next to jar (or other locations described in [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config)

## Running
### From precompiled libraries 
```
java -jar mokka*.jar
```

### Directly from sources
```
cd src/mokka
mvn spring-boot:run # (use -Pdev if in development mode)
```

## Usage
```
- http://localhost:8081 - login page
- http://localhost:8081/bluemedia?ServiceID=123123111&OrderID=11&Amount=0.00&CustomerEmail=test@test.pl&Hash=b05bc1f89b61d68b57eacf83d28f79b6f9dc7e9b20e77b0d3f676475721f7 - exemplary payment page
- http://localhost:8081/files - serving files from directory on local file system
```

## Debugging
### Logs
Logback is used as logging framework. By default logs are redirected to FILE (/tmp/mokka.log).
### Identifying mocks
`X-Mock-Id` HTTP header is added to mock response to allow easy identification of related mock configuration
