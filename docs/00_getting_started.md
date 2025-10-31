# 🚀 Руководство по началу работы

## Обзор проекта
Task Manager API демонстрирует ключевые аспекты построения архитектуры веб-приложений.
Проект полезен разработчикам, которые хотят правильно создавать веб-приложения любого уровня и 
сложности на фреймворке [Spring Boot](https://spring.io/guides/gs/spring-boot).

На примере простого менеджера задач демонстрируется готовая 
архитектура, которая включает в себя:

* логирование ошибок
* работу с базой данных
* организацию безопасности данных
* тестирование кода
* применение различных паттернов и принципов проектирования

Приложение представляет собой [REST API](https://ru.wikipedia.org/wiki/REST) для управления задачами со стандартным набором [CRUD-операций](https://ru.wikipedia.org/wiki/CRUD). 

!!! note "Примечание"
    В проекте отражена лишь часть рекомендуемых практик и принципов, которых достаточно для понимания основ построения правильной 
    архитектуры веб-приложений. 

### Целевая аудитория
- Java-разработчики, изучающие Spring Boot

## Предварительные требования

### Технологический стек

- [JDK 21.0.6](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- Spring Boot 3.5.5
- Spring Data JPA
- [PostgreSQL 17](https://www.postgresql.org/download/)
- Springdoc OpenAPI
- Maven

!!! note "Примечание"
    Вы также можете использовать версию PostgreSQL 14 или выше.

### Проверка установки

Убедитесь, в правильной версии установленного ПО. Для этого выполните следующие команды в командной строке:

Для проверки версии Java:
```bash
java -version
```

Для проверки версии PostgreSQL:
```bash
psql --version
```
## Установка и настройка

### 1. Клонирование репозитория

Перейдите в целевую директорию, где хотите разместить проект:
```bash
cd <Путь_к_папке>
```

Затем клонируйте репозиторий:
```bash
git clone https://github.com/your-username/spring-boot-best-practices-demo.git
```


### 2. Настройка базы данных
Для предварительной настройки учетной записи и конфигурации базы данных, воспользуйтесь следующими командами:

1. Подключитесь к PostgreSQL как администратор
    ```bash
    psql -U postgres
    ```

2. Создайте базу данных
    ```bash
    CREATE DATABASE taskmanager_db;
    ```

3. Создайте пользователя
    ```bash
    CREATE USER taskuser WITH PASSWORD 'taskpass';
    ```

4. Назначьте привилегии
    ```bash
    GRANT ALL PRIVILEGES ON DATABASE taskmanager_db TO taskuser;
    ```

5. Подключитесь к новой базе данных
    ```bash
    \c taskmanager_db
    ```

6. Назначьте привилегии на схему
    ```bash
    GRANT ALL ON SCHEMA public TO taskuser;
    ```


### 3. Настройка конфигурации проекта
Откройте проект. Убедитесь что файл `src/main/resources/application.properties` имеет следующий вид:

```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.operationsSorter=alpha
springdoc.swagger-ui.enabled=true

spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager_db
spring.datasource.username=taskuser
spring.datasource.password=taskpass
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=create-drop

#spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
#spring.datasource.username=sa
#spring.datasource.password=
#spring.jpa.hibernate.ddl-auto=create-drop
#spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### 4. Запуск приложения

Для запуска приложения воспользуйтесь следующими способами:

**Через Maven:**

```bash
./mvnw spring-boot:run
```

**Через IDE:**

1. Откройте проект в [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) или [Eclipse](https://www.eclipse.org/downloads/packages/)
2. Найдите класс `TaskManagerApplication.class`
3. Запустите приложение нажав на кнопку запуска - ![запуск приложения](assets/run_pic.png) (в IntelliJ IDEA)

### 5. Проверка работоспособности

После запуска проверьте:

* В терминале должно появиться сообщение об успешно запущенном приложении.
* Убедитесь, что подключенный [Swagger UI](https://github.com/swagger-api/swagger-ui) доступен и работает. Для этого пройдите по ссылке: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* Спецификация OpenAPI: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Тестовые данные

После первого запуска выполните в psql:
```sql
INSERT INTO tasks (title, description, status, created_at) VALUES
    ('Разработка приложения', 'Спроектировать простое приложение на Spring Boot', 'DONE', NOW()),
    ('Тестирование API', 'Протестировать приложение на разных уровнях', 'DONE', NOW()),
    ('Оптимизация производительности', 'Проанализировать и улучшить производительность приложения', 'TODO', NOW()),
    ('Деплой на сервер', 'Развернуть приложение на сервере', 'IN_PROGRESS', NOW());
```

#### Проверка через API

Для проверки работоспособности выполните следующие [HTTP-запросы](https://ru.wikipedia.org/wiki/HTTP#Структура_HTTP-сообщения):

Получить список всех задач:

```http
GET /api/tasks HTTP/1.1
```

Создать новую задачу:

```http
POST /api/tasks HTTP/1.1
Host: localhost:8080 
Content-Type: application/json
Accept: application/json

{
  "title": "New Task-123",
  "description": "Example of a new task description",
  "status": "TODO"
}
```

Более подробно об API описано в разделе "[Справочник API](06_api_reference.md)"

Для отправки HTTP-запросов, Вы можете воспользоваться одним из следующих инструментов на выбор:

* [Postman](https://www.postman.com/downloads/) 
* HTTP-client в IDE [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)  (`Tools -> HTTP Client - Create Request in HTTP Client`)
* плагин HTTP4e (если пользуетесь IDE [Eclipse](https://www.eclipse.org/downloads/packages/))  
* [cURL-запросы](https://curl.se/)

<hr>

<footer class="footer-nav">
  <a href="../" class="footer-nav__link footer-nav__link--prev">
    <span class="footer-nav__icon">←</span>
    <span class="footer-nav__title">Начало работы</span>
  </a>

  <a href="../01_architecture/" class="footer-nav__link footer-nav__link--next">
    <span class="footer-nav__title">Архитектура приложения</span>
    <span class="footer-nav__icon">→</span>
  </a>
</footer>