# Используем официальный JDK 21 образ как базовый
FROM eclipse-temurin:21-jdk as builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем Gradle скрипты и зависимости (для кеширования)
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

# Загружаем зависимости (будет кешироваться)
RUN ./gradlew dependencies

# Копируем остальной код
COPY . .

# Собираем приложение
RUN ./gradlew bootJar

# Финальный образ
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Копируем jar из предыдущего шага
COPY --from=builder /app/build/libs/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
