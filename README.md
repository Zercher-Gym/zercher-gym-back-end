# Zercher Gym - Back-end

This is the back-end component of the Zercher Gym application.
The application is a Spring Boot REST API built using Java 24.
The database migrations are handled by Liquibase.

## Prerequisites

We recommend setting up the application on Linux.
You will have to install the following tools/programs in order to get the back-end running:

- [OpenJDK 24](https://openjdk.org/projects/jdk/24/)
- [Maven](https://maven.apache.org/install.html)
- [Liquibase CLI](https://www.liquibase.com)
- [PostgreSQL](https://www.postgresql.org)
- [pgAdmin4](https://www.pgadmin.org)

In addition, you can skip running PostgreSQL natively and just start a
Docker [container](https://hub.docker.com/_/postgres).
Also, we recommend running the project using a modern IDE such
as [IntelliJ IDEA Community Version](https://www.jetbrains.com/idea/)

## Initialization

Currently, Liquibase has been configured and tested to run only with PostgreSQL.
In the `liquibase.properties` file, alter the `url`, `username` and `password` to match those of the PostgreSQL instance
you wish to connect to.
[Here](https://www.baeldung.com/java-jdbc-url-format#jdbc-url-format-for-postgresql) you can find an example of
connection URL.

After setting these credentials, go in the project root and run

```shell
mvn liquibase:update
```

If the credentials you entered were correct, when you access your database with a management tool such
as [pgAdmin](https://www.pgadmin.org) you should see a series of tables in the `public` schema.
The application comes pre-configured with data and can run straight out of the box.

## Starting the application

In order to start the application, you may simply enter the command:

```shell
mvn spring-boot:run
```

Make sure you have the correct version of Java enabled.
Alternatively you may run the project from an IDE by simply running the `ZercherApplication` class.
If any errors arise when you run the main class, then please check your database connection credentials.

## Documentation
Upon application start, the application documentation can be found at `http://localhost:8080/swagger-ui/index.html`.
Additionally, the API documentation can be found in JSON format at `http://localhost:8080/v3/api-docs`.