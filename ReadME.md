# Описание проекта:

- authentication-service - Сервис аутентификации, авторизации и регистрации. Работает с JWT токенами. Настроен swagger
  для проверки
  common-dto - Общий модуль содержит dto который используется в kafka.
- email-notification-service - Сервис для отправки уведомлений из kafka топика

## Реализация Kafka

Реализованы kafka consumer producer со spring kafka и apache kafka.

- Выбор через проперти
    - application:kafka-notification-topic:consumer-type
    - application:kafka-notification-topic:producer-type
    - со значениями spring или native

# Инструкция по запуску:

Сборка:

    mvn clean build -dSkipTests

docker-compose.yaml в корне проекта поднимает kafka кластер:

    docker-compose up -d

запуск бд для сервиса аутентификации:

    cd ./authentication-service
    docker-compose up -d
    cd ..

запуск сервиса аутентификации:

    java -jar authentication-service\target\authentication-service-0.0.1-SNAPSHOT.jar

запуск сервиса уведомлений:

    java -jar email-notification-service\target\email-notification-service-0.0.1-SNAPSHOT.jar

## swagger для проверки

http://localhost:8080/swagger-ui/index.html#/