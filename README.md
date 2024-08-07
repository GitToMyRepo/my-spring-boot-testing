# my-spring-boot-testing

This project was duplicated from [Unit and Integration Testing in Spring Boot Micro Service](https://salithachathuranga94.medium.com/unit-and-integration-testing-in-spring-boot-micro-service-901fc53b0dff) and its [Git Repository](https://github.com/SalithaUCSC/spring-boot-testing).

This is an experimental project for learning unit testing and integration testing for Spring boot application.

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

**Both Database and _OrderServiceApplication_ must be running before executing _OrderApiIntegrationTest_.**

### Use Database

```
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| order-db           |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.00 sec)

mysql> use order-db
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+--------------------+
| Tables_in_order-db |
+--------------------+
| orders             |
+--------------------+
1 row in set (0.00 sec)

mysql> select * from orders;
+----+-------+--------+-----+
| id | buyer | price  | qty |
+----+-------+--------+-----+
| 29 | Ada   |     25 |   1 |
| 30 | peter |     30 |   3 |
| 32 | Bob   | 125.01 |  10 |
| 33 | Chris | 134.56 |  12 |
| 34 | David |    108 |  13 |
+----+-------+--------+-----+
5 rows in set (0.00 sec)

mysql>
```