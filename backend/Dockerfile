FROM eclipse-temurin:17-jre-jammy
ARG MODULE
WORKDIR /app
COPY ${MODULE}/target/${MODULE}-1.0.0.jar /app/app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65 -XX:+ExitOnOutOfMemoryError"
USER 10001
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
