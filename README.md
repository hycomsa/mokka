# Mokka
---
[![Build Status](https://travis-ci.org/hycomsa/mokka.svg?branch=master)](https://travis-ci.org/hycomsa/mokka)

##Build
```
cd src
mvn clean package
```

##Run

Before running Mokka make sure you have Oracle database running and configured. By default Mokka expects `oracle` schema on `oracle` user identified by `oracle`.
You can change those properties directly in `application.properties` files or (recommended) by overwriting during start (see below).

```
cd mokka
mvn spring-boot:run
```

or

```
java -jar target/mokka*.jar --spring.config.location=your_app.properties
```


URLs:
- http://localhost:8081 - login page
- http://localhost:8081/bluemedia?ServiceID=1&OrderID=11&Amount=1.00&CustomerEmail=&Hash=1 - exemplaty payment page
- http://localhost:8081/files - serving files from directory on local file system

##Logs
Logback is used as logging framework. By default logs are redirected to CONSOLE and FILE (/tmp/mokka.log).
