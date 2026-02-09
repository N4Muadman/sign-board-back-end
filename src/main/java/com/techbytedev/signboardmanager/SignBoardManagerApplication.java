package com.techbytedev.signboardmanager;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@Slf4j
public class SignBoardManagerApplication {

    public static void main(String[] args) {
        // Tải file .env trước khi khởi động Spring Boot
        Dotenv dotenv = Dotenv.configure()
            .directory(System.getProperty("user.dir")) // Sử dụng thư mục làm việc hiện tại
            .ignoreIfMissing() // Không throw lỗi nếu file .env không tồn tại
            .load();

        // Đặt các biến từ .env vào System properties
        if (dotenv.entries().isEmpty()) {
            System.out.println("Warning: No environment variables loaded from .env file. Check if the file exists and is correctly formatted.");
        } else {
            dotenv.entries().forEach(entry -> {
                System.out.println("Loading env variable: " + entry.getKey() + "=" + entry.getValue());
                System.setProperty(entry.getKey(), entry.getValue());
            });
        }

       
        // Khởi động ứng dụng Spring Boot
        SpringApplication.run(SignBoardManagerApplication.class, args);
    }
}