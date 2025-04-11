FROM openjdk:17-jdk-slim
# Establece el archivo JAR a copiar (el build generado por Maven)
ARG JAR_FILE=target/blog-0.0.1.jar

# Copia el JAR al contenedor y le pone un nombre estándar
COPY ${JAR_FILE} app_blog.jar

# Expone el puerto que usa Spring Boot
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app_blog.jar"]