Mokka
---
[![Build Status](https://travis-ci.org/hycomsa/mokka.svg?branch=master)](https://travis-ci.org/hycomsa/mokka)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=pl.hycom.mokka%3Amokka-parent&metric=alert_status)](https://sonarcloud.io/dashboard?id=pl.hycom.mokka%3Amokka-parent)

## Prerequisities
Database of your choice: Oracle, MySQL, HSQLDB, H2.

## Install
### Precompiled binaries

Precompiled binary for release version is available on [GithHub Releases page](https://github.com/hycomsa/mokka/releases).
### Building from sources
```
cd src
mvn clean package
```
## Configuration
The minimal configuration set requires to provide datasource configuration:
```
spring.datasource.url=
spring.datasource.username = 
spring.datasource.password = 
```
in `application.properties` file next to jar.

## Running
### From precompiled libraries 
```
java -jar target/mokka*.jar
```

### Directly from sources
```
cd src/mokka
mvn spring-boot:run # (use -Pdev if in development mode)
```

## Usage
- http://localhost:8081 - login page
- http://localhost:8081/bluemedia?ServiceID=1&OrderID=11&Amount=1.00&CustomerEmail=&Hash=1 - exemplary payment page
- http://localhost:8081/files - serving files from directory on local file system

## Debugging
### Logs
Logback is used as logging framework. By default logs are redirected to FILE (/tmp/mokka.log).
### Identifying mocks
`X-Mock-Id` HTTP header is added to mock response to allow easy identification of related mock configuration
