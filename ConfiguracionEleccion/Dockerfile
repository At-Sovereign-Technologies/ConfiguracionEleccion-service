FROM eclipse-temurin:17-jdk AS compilacion
WORKDIR /app

COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY pom.xml pom.xml
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=compilacion /app/target/ConfiguracionEleccion-0.0.1-SNAPSHOT.jar aplicacion.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/aplicacion.jar"]