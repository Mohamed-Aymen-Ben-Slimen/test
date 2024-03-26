# Warehouse

How to start the Warehouse application
---

1. Run `mvn clean package` to build your application
1. Start application with `java -jar target/warehouse-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`

Documentation
---

This project is configured with Swagger.
Enter url `http://localhost:8080/swagger`.
You can see the interactive documentation, and you can send requests to create, list and update widgets through Swagger UI.
