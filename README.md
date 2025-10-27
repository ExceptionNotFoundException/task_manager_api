# Best Practices: Task Manager API Guide

[![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apache-maven)](https://maven.apache.org/)
[![Java](https://img.shields.io/badge/Java-21-orange?style=flat&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.5-brightgreen?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat&logo=postgresql)](https://www.postgresql.org/)
[![SpringDoc](https://img.shields.io/badge/SpringDoc_OpenAPI-2.8.5-brightgreen?style=flat&logo=swagger)](https://springdoc.org/)

## 👀 Описание

Проект демонстрирует лучшие практики (best practices) построения архитектуры приложений на Spring Boot.
В основе лежит простое приложение для управления задачами, в реализации которого отражено применение этих подходов.

## 🎯 Цели проекта

- Показать на примере профессиональные подходы к проектированию API
- Создать руководство по best practices для начинающих разработчиков

## 📋 Технологический стек

- JDK 21.0.6
- Spring Boot 3.5.5
- Spring Data JPA
- PostgreSQL 14+
- Springdoc OpenAPI
- Maven

## 🚀 Быстрый старт

### Предварительные требования

Убедитесь, что у Вас установлены необходимые версии PostgreSQL и JDK.

### 1. Клонирование репозитория
Клонируйте репозиторий:

```bash
git clone https://github.com/your-username/spring-boot-best-practices-demo.git
cd spring-boot-best-practices-demo
```
Создайте базу данных в PostgreSQL:

```sql
CREATE DATABASE taskmanager_db;
```

### 2. Настройка подключения к БД
Отредактируйте `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager_db
spring.datasource.username=taskuser
spring.datasource.password=taskpass
spring.jpa.hibernate.ddl-auto=create-drop
```

### 3. Запуск приложения

```bash
./mvnw spring-boot:run
```
Приложение будет доступно по адресу `http://localhost:8080`

### 4. Тестовые данные
После запуска, для заполнения таблицы тестовыми данными, выполните в SQL-консоли следующую команду:

```sql
INSERT INTO tasks (title, description, status, created_at) VALUES
    ('Разработка приложения', 'Спроектировать и написать простое приложение на Spring Boot', 'DONE', NOW()),
    ('Тестирование API', 'Протестировать приложение на разных уровнях', 'DONE', NOW()),
    ('Оптимизация производительности', 'Проанализировать и улучшить производительность приложения', 'TODO', NOW()),
    ('Деплой на сервер', 'Развернуть приложение на сервере', 'IN_PROGRESS', NOW()
);
```

## 📖 Документация API

* **Swagger UI:** http://localhost:8080/swagger-ui.html
* **OpenAPI:** http://localhost:8080/api-docs

## 🌐 Конечные точки API (Endpoints)
| Method | Endpoint        | Description                  |
|--------|-----------------|------------------------------|
| GET    | /api/tasks      | Получить список всех задач   |
| GET    | /api/tasks/{id} | Получить задачу по ID        |
| POST   | /api/tasks      | Создать новую задачу         |
| PUT    | /api/tasks/{id} | Обновить существующую задачу |
| DELETE | /api/tasks/{id} | Удалить задачу               |

### Пример запроса

Для создания новой задачи отправьте следующий HTTP-запрос:

**Пример тела запроса `POST` на `/api/tasks`:**

```json
{
  "title": "new_task_125",
  "description": "a task that was created as an example",
  "status": "IN_PROGRESS"
}
```

**Пример успешного ответа:**
```json
{
  "id": 1,
  "title": "new_task_125",
  "description": "a task that was created as an example",
  "status": "IN_PROGRESS",
  "createdAt": "2025-01-01T10:30:00"
}
```

## 🧪 Тестирование

Для запуска всех тестов используйте следующую команду:

```bash
./mvnw test
```

## 📖 Полная документация

Более подробно ознакомиться с проектом можно [здесь](https://ExceptionNotFoundException.github.io/task_manager_api/).

## 📄 Лицензия

Проект распространяется под лицензией **MIT**. Вы можете свободно изменять, адаптировать и распространять код, 
а также использовать его в приватных и коммерческих проектах. 

Ознакомиться подробнее с MIT-лицензией: [LICENSE](https://github.com/ExceptionNotFoundException/task_manager_api?tab=License-1-ov-file)

## 👨‍💻 Автор

Vladislav Gabergan

GitHub: [ExceptionNotFoundException](https://github.com/ExceptionNotFoundException)

Email: gabergan90@gmail.com

---
_Проект создан как портфолио технического писателя и Java-разработчика, демонстрирующий профессиональные подходы к созданию enterprise-приложений._
