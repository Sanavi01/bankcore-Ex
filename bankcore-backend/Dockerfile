# ====================================================================
# STAGE 1: Compilación del artefacto JAR con Gradle (Build Stage)
# ====================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /home/gradle/project

# Copiar scripts del wrapper de Gradle para aprovechar caché de capas
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Corregir saltos de línea Windows (CRLF a LF) y otorgar permisos de ejecución al wrapper
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Descargar dependencias de Gradle en caché
RUN ./gradlew dependencies --no-daemon || true

# Copiar código fuente y compilar JAR excluyendo pruebas unitarias para acelerar build
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# ====================================================================
# STAGE 2: Runtime Seguro sin privilegios de root (Production Stage)
# ====================================================================
FROM eclipse-temurin:21-jre-alpine

# Estándar de Ciberseguridad Bancaria (OWASP / CIS Benchmark):
# Crear usuario y grupo de sistema sin privilegios de root para mitigar escalada de privilegios
RUN addgroup -S appgroup && adduser -S appuser -G appgroup -u 10001

WORKDIR /app

# Copiar el archivo ejecutable JAR compilado en la fase anterior
COPY --from=builder --chown=appuser:appgroup /home/gradle/project/build/libs/*.jar app.jar

# Conmutar al usuario de mínimos privilegios
USER appuser:appgroup

# Variables de entorno por defecto (sobrescribibles por Docker Compose o Kubernetes)
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE="default"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
