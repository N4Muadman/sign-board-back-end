# Sử dụng image OpenJDK 17 làm base image
FROM openjdk:17-jdk-slim

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép file pom.xml và tải dependencies
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline

# Sao chép mã nguồn
COPY src ./src

# Build ứng dụng
RUN ./mvnw package -DskipTests

# Expose cổng 8080
EXPOSE 8080

# Chạy ứng dụng
CMD ["java", "-jar", "target/SignBoardManager-0.0.1-SNAPSHOT.jar"]