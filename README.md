# Cloud file storage service

Приложение - REST-сервис.

Сервис предназначен для загрузки файлов и вывода списка уже загруженных файлов пользователя.

REST-сервис работает на http://localhost:8080/api/v1.

---

## Запуск сервиса:

1. Клонировать из github:
#### git clone
2. Собрать архивный файл:
#### mvn package
3. Построить Docker image:
#### docker build -t rest_storage .
4. Запустить:
#### docker compose up -d

---

## FRONT:
https://github.com/kievsan/jd-40-homeworks/tree/master/diploma/netology-diplom-frontend