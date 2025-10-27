# 📋 Справочник API

Task Manager предоставляет набор [CRUD-операций](https://ru.wikipedia.org/wiki/CRUD) для управления задачами, спроектированный по принципам [REST](https://ru.wikipedia.org/wiki/REST).
В справочнике [API](https://ru.wikipedia.org/wiki/API) описана структура HTTP-запросов и ответов - формат данных и времени, заголовки запросов, 
конечные точки и коды статусов HTTP-ответов.

## Форматы данных

API использует формат [JSON](https://ru.wikipedia.org/wiki/JSON) для всех запросов и ответов. Данные передаются в структурированном виде с соблюдением 
стандартных типов: строки, числа, булевы значения и даты в формате [ISO 8601](https://ru.wikipedia.org/wiki/ISO_8601).

### 1. Статусы задач - TaskStatus

|   Статус    |           Описание           |
|:-----------:|:----------------------------:|
|    TODO     | Задача создана, но не начата |
| IN_PROGRESS |       Задача в работе        |
|    DONE     |       Задача выполнена       |

### 2. Формат даты и времени

Используется стандарт ISO 8601:

```text
2025-01-21T12:30:59
```

## Конечные точки

**Конечная точка (endpoint)** - это определенный URL, по которому клиент может отправить запрос к API 
для получения, изменения, обновления или удаления данных. 

Ниже приведены примеры запросов для каждой конечной точки, а также их ответы и возможные ошибки.

### 1. Получить список всех задач

!!! warning "Внимание"
    Перед выполнением запросов, убедитесь что в вашей базе данных уже присутствуют созданные задачи.

`GET /api/tasks` - получает список всех задач в системе.

**Заголовки**

| Название |     Значение     |
|:--------:|:----------------:|
|   Host   |  localhost:8080  |
|  Accept  | application/json |

**Пример полного запроса**

```http
GET /api/tasks HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Пример успешного ответа (200 OK)**

```json
[
  {
    "id": 1,
    "title": "Изучить Spring Boot",
    "description": "Освоить основы Spring Boot framework",
    "status": "IN_PROGRESS",
    "createdAt": "2025-10-10T16:36:02.95536"
  },
  {
    "id": 2,
    "title": "Написать документацию",
    "description": "Создать полную техническую документацию",
    "status": "TODO",
    "createdAt": "2025-10-10T16:36:02.95536"
  }
]
```

**Коды статуса**

* **200 OK** - задачи успешно получены
* **500 Internal Server Error** - внутренняя ошибка сервера

### 2. Получить задачу по ID

`GET /api/tasks/{id}` - получить конкретную задачу.

**Параметры**

| Параметр  | Тип  | Обязательный  | Описание  |
|:---------:|:----:|:-------------:|:---------:|
|    id     | Long |      Да       | ID задачи |

**Заголовки**

| Название |     Значение     |
|:--------:|:----------------:|
|   Host   |  localhost:8080  |
|  Accept  | application/json |

**Пример запроса**

```http
GET /api/tasks/1 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Пример успешного ответа (200 OK)**

```json
{
  "id": 1,
  "title": "Изучить Spring Boot",
  "description": "Освоить основы Spring Boot framework",
  "status": "IN_PROGRESS",
  "createdAt": "2025-10-10T16:36:02.95536"
}
```

**Пример ошибки (404 Not Found)**

```json
{
  "timestamp": "2025-10-10T16:36:02.95536",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Task not found with id: 999",
  "path": "/api/tasks/999"
}
```

**Коды статуса**

* **200 OK** - задача по указанному ID успешно получена
* **400 Bad Request** - невалидный ID
* **404 Not Found** - задача не найдена
* **500 Internal Server Error** - внутренняя ошибка сервера

### 3. Создать новую задачу

`POST /api/tasks` - создает новую задачу в системе.

**Заголовки**

|   Название   |     Значение     |
|:------------:|:----------------:|
|     Host     |  localhost:8080  |
| Content-Type | application/json |
|    Accept    | application/json |

**Тело запроса (TaskRequest)**

|    Поле     |    Тип     | Обязательный  |             Ограничения              |    Описание     |
|:-----------:|:----------:|:-------------:|:------------------------------------:|:---------------:|
|    title    |   String   |      Да       |            1-100 символов            | Название задачи |
| description |   String   |      Нет      |           до 500 символов            | Описание задачи |
|   status    | TaskStatus |      Да       | только статусы TODO/IN_PROGRESS/DONE |  Статус задачи  |

**Пример запроса**

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

**Пример успешного ответа (201 CREATED)**

```json
{
  "id": 3,
  "title": "New Task-123",
  "description": "Example of a new task description",
  "status": "TODO",
  "createdAt": "2025-10-10T16:36:02.95536"
}
```

**Ошибки валидации (400 Bad Request)**

```json
{
  "timestamp": "2025-10-10T16:36:02.95536",
  "status": 400,
  "error": "Validation Failed",
  "message": "Validation failed for one or more fields",
  "path": "/api/tasks",
  "validationErrors": [
    {
      "field": "title",
      "message": "Title is required",
      "rejectedValue": null
    },
    {
      "field": "status",
      "message": "Status is required",
      "rejectedValue": null
    }
  ]
}
```

**Коды статуса**

* **201 CREATED** - задача успешно создана
* **400 Bad Request** - невалидные данные
* **500 Internal Server Error** - внутренняя ошибка сервера

### 4. Обновить задачу

`PUT /api/tasks/{id}` - обновляет существующую задачу. Все поля являются опциональными - передавайте только те, которые нужно изменить.

**Заголовки**

|   Название   |     Значение     |
|:------------:|:----------------:|
|     Host     |  localhost:8080  |
| Content-Type | application/json |
|    Accept    | application/json |

**Параметры**

| Параметр  | Тип  | Обязательный  |       Описание        |
|:---------:|:----:|:-------------:|:---------------------:|
|    id     | Long |      Да       | ID обновляемой задачи |

**Тело запроса (TaskRequest)**

|    Поле     |    Тип     | Обязательный  |      Ограничения      |       Описание        |
|:-----------:|:----------:|:-------------:|:---------------------:|:---------------------:|
|    title    |   String   |      Нет      |    1-100 символов     | Новое название задачи |
| description |   String   |      Нет      |    до 500 символов    | Новое описание задачи |
|   status    | TaskStatus |      Нет      | TODO/IN_PROGRESS/DONE |  Новый статус задачи  |

**Пример запроса**

```http
PUT /api/tasks/1 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: application/json

{
  "title": "New Task-123",
  "description": "Example of a new task description",
  "status": "IN_PROGRESS"
}
```

**Пример успешного ответа (200 OK)**

```json
{
  "id": 1,
  "title": "New Task-123",
  "description": "Example of a new task description",
  "status": "IN_PROGRESS",
  "createdAt": "2025-10-10T16:36:02.95536"
}
```

**Пример ошибки (404 Not Found)**

```json
{
  "timestamp": "2025-10-10T16:36:02.95536",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Task not found with id: 999",
  "path": "/api/tasks/999"
}
```

**Коды статуса**

* **200 OK** - задача успешно обновлена
* **400 Bad Request** - невалидные данные или ID
* **404 Not Found** - задача не найдена
* **500 Internal Server Error** - внутренняя ошибка сервера

### 5. Удалить задачу

`DELETE /api/tasks/{id}` - удаляет задачу из системы.

**Заголовки**

| Название |     Значение     |
|:--------:|:----------------:|
|   Host   |  localhost:8080  |

**Параметры**

| Параметр  | Тип  | Обязательный  |      Описание       |
|:---------:|:----:|:-------------:|:-------------------:|
|    id     | Long |      Да       | ID удаляемой задачи |

**Пример запроса**

```http
DELETE /api/tasks/1 HTTP/1.1
Host: localhost:8080
```

**Успешный ответ (204 No Content)**

```http
HTTP/1.1 204 No Content
```

**Коды статуса**

* **400 Bad Request** - невалидный ID
* **404 Not Found** - задача не найдена
* **500 Internal Server Error** - внутренняя ошибка сервера

## Формат ошибок

### Стандартный формат ошибки

Все ошибки возвращаются в едином формате:

```json
{
  "timestamp": "2025-10-10T16:36:02.95536",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Task not found with id: 3",
  "path": "/api/tasks/3"
}
```

## Коды статусов

| Код  |       Значение        |          Использование          |
|:----:|:---------------------:|:-------------------------------:|
| 200  |          OK           | Успешные GET, PUT, POST запросы |
| 204  |      No Content       |        Успешное удаление        |
| 400  |      Bad Request      | Невалидные данные или параметры |
| 404  |       Not Found       |      Ресурс не существует       |
| 500  | Internal Server Error |    Внутренняя ошибка сервера    |

## Примеры запросов cURL

### Получить все задачи

```bash
curl -X GET http://localhost:8080/api/tasks \
  -H "Accept: application/json"
```

### Создать задачу

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "title": "Новая задача через cURL",
    "description": "Созданная с помощью командной строки",
    "status": "TODO"
  }'
```

### Обновить задачу

```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "title": "Измененное название",
    "description": "Измененное описание",
    "status": "DONE"
  }'
```

### Удалить задачу

```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```

## Swagger документация

API также задокументировано через Swagger UI:

* **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Спецификация OpenAPI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Аннотации в коде

Контроллеры используют аннотации [SpringDoc](https://springdoc.org/) для автоматической документации:

```java
@Operation(summary = "Get task by ID", description = "Returns a single task by its ID")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Task found"),
    @ApiResponse(responseCode = "404", description = "Task not found")
})
@GetMapping("/{id}")
public ResponseEntity<TaskResponse> getTask(
    @Parameter(description = "ID of the task to retrieve") 
    @PathVariable Long id) {
    ...
}
```

<hr>