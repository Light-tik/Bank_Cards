## Инструкция по локальному запуску

1. Склонируйте проект с репозитория, либо скачайте зип-архив
2. Установите все необходимые зависимости, сбилдите проект, для разработки использовалась SDK: graalvm-jdk-21 GraalVM 21.0.6 - VM 23.1.6, JavaLanguageVersion -21
3. Запустите докер (если у вас Windows, то Docker Desktop), поднимите контейнеры
 ```bash
    docker-compose up
```
4. Далее запустите сам проект
5. Чтобы посмотреть документацию, необходимо после запуска проекта перейти по этой ссылке

http://localhost:8080/swagger-ui/index.html#/

6. Проверить работу можно с помощью Postman, отправляя интересующие вас запросы, ориентируясь, но не забывая про токен
7. Токен выдаётся после регистрации, либо входа в систему
8. Запросы без токена возможны только на открытие  документации(Swagger), регистрацию и вход в систему.