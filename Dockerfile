FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Declare the data directory as a volume mount point so H2 files
# written to /app/data persist on the host when a bind-mount or
# named volume is attached at runtime.
VOLUME /app/data

COPY target/TrainOrder-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
