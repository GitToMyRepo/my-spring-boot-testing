# my-spring-boot-testing

This project was duplicated from [Unit and Integration Testing in Spring Boot Micro Service](https://salithachathuranga94.medium.com/unit-and-integration-testing-in-spring-boot-micro-service-901fc53b0dff) and its [Git Repository](https://github.com/SalithaUCSC/spring-boot-testing).

## MySQL Database
### Create and start up MySQL container from a MySQL image with latest tag

```
docker run -p 3306:3306 --name mysql-order-db -e MYSQL_ROOT_PASSWORD=root-order-db -e MYSQL_DATABASE=order-db mysql:latest
```

### Access the database

```
PS C:\Users\kenwu> docker exec -it mysql-order-db bash
bash-4.4# mysql -uroot -proot-order-db
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 8
Server version: 8.2.0 MySQL Community Server - GPL

Copyright (c) 2000, 2023, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
```

