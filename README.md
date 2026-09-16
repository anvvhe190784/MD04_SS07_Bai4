# MD04_SS07 - Microservices Configuration Management
## [Bài tập 4 - Giỏi] Tự động cập nhật cấu hình với @RefreshScope (Gradle)

### 1. Mục tiêu
- **Kiến thức**: Thay đổi giá trị biến cấu hình tức thì mà không cần khởi động lại Server (Zero-Downtime Hot Reloading).
- **Kỹ năng**: Sử dụng Spring Cloud Config kết hợp Spring Boot Actuator endpoint `/actuator/refresh` và annotation `@RefreshScope`.

---

### 2. Kiến trúc & Cấu trúc Thư mục
```text
MD04_SS07_Bai4/
├── .gitignore
├── README.md
├── medical-config-repo/
│   └── patient-service.properties        <-- Chứa thuộc tính app.welcome
├── config-server/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew / gradlew.bat
│   └── src/
└── patient-service/
    ├── build.gradle                      <-- Đã thêm spring-boot-starter-actuator
    ├── settings.gradle
    ├── gradlew / gradlew.bat
    └── src/
        └── main/java/com/medical/patientservice/controller/
            └── WelcomeController.java    <-- Controller đánh dấu @RefreshScope
```

---

### 3. Cấu hình Chi Tiết

#### A. Cấu hình trên Git Repo (`medical-config-repo/patient-service.properties`):
```properties
server.port=8081
app.welcome=Chào mừng tới BV RikkeiAcademy
spring.datasource.url=jdbc:postgresql://localhost:5432/patient_db
spring.datasource.username=postgres
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update
```

#### B. Cấu hình Patient Service (`patient-service/src/main/resources/application.properties`):
```properties
server.port=8081
spring.application.name=patient-service
spring.config.import=optional:configserver:http://localhost:8888

# Mở endpoint Actuator bao gồm /actuator/refresh
management.endpoints.web.exposure.include=*
```

#### C. Lớp `WelcomeController.java`:
```java
@RestController
@RefreshScope
public class WelcomeController {

    @Value("${app.welcome:Chào mừng}")
    private String welcomeMessage;

    @GetMapping("/welcome")
    public String getWelcomeMessage() {
        return welcomeMessage;
    }
}
```

---

### 4. Quy trình Kiểm thử Tự động Cập nhật (Zero-Downtime)

#### Bước 1: Khởi động Config Server & Patient Service
```bash
# Terminal 1: Khởi động Config Server
cd config-server
.\gradlew.bat bootRun

# Terminal 2: Khởi động Patient Service
cd patient-service
.\gradlew.bat bootRun
```

#### Bước 2: Kiểm tra giá trị ban đầu
Truy cập: [http://localhost:8081/welcome](http://localhost:8081/welcome)
👉 Kết quả: `Chào mừng tới BV RikkeiAcademy`

#### Bước 3: Thay đổi cấu hình trên Git
Sửa file `medical-config-repo/patient-service.properties`:
```properties
app.welcome=Chào mừng tới BV RikkeiEducation
```
Sau đó commit vào Git:
```bash
git add .
git commit -m "update welcome message"
git push origin main
```

#### Bước 4: Kích hoạt Refresh không cần khởi động lại service
Gửi yêu cầu POST tới Actuator:
```bash
curl -X POST http://localhost:8081/actuator/refresh
```
Phản hồi: `["app.welcome"]`

#### Bước 5: Kiểm tra kết quả sau khi refresh
Truy cập lại: [http://localhost:8081/welcome](http://localhost:8081/welcome)
👉 Kết quả ngay lập tức đổi thành: `Chào mừng tới BV RikkeiEducation` mà service không hề bị ngắt kết nối!