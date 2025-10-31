# ⚡ Обработка исключений

Исключение (Exception) — это событие, возникающее во время выполнения программы, которое нарушает 
её нормальный поток выполнения и представляет собой объект, содержащий информацию об ошибке.

В проекте реализована централизованная система обработки исключений,
которая обеспечивает согласованный формат ошибок, логирование и HTTP-статусы для возможных сценариев ошибок.

## Структура классов

Пакет `com.example.taskmanager.exception`

* `GlobalExceptionHandler`
* `InternalServerException`
* `ResourceNotFoundException`
* `WrongRequestException`

## Архитектура обработки исключений

**Методы `GlobalExceptionHandler`**

|                Метод                 |     Обрабатываемые исключения     | HTTP Status |                         Описание                          |
|:------------------------------------:|:---------------------------------:|:-----------:|:---------------------------------------------------------:|
|  `handleResourceNotFoundException`   |    `ResourceNotFoundException`    |     404     |             Отсутствие запрашиваемого ресурса             |
|    `handleWrongRequestException`     |      `WrongRequestException`      |     400     |              Некорректные запросы от клиента              |
|   `handleInternalServerException`    |     `InternalServerException`     |     500     | Внутренние ошибки сервера с сохранением трассировки стека |
|     `handleValidationExceptions`     | `MethodArgumentNotValidException` |     400     |       Ошибки валидации DTO с детализацией по полям        |
| `handleConstraintViolationException` |  `ConstraintViolationException`   |     400     |              Нарушения constraints валидации              |
|     `handleAllUncaughtException`     |            `Exception`            |     500     |              Обработка остальных исключений               |


Класс `GlobalExceptionHandler` с аннотацией `@RestControllerAdvice` перехватывает и обрабатывает все возможные исключения:

**Пример кода `GlobalExceptionHandler`**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(...)
        
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(...)
        
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUncaughtException(...)
}
```

### Иерархия пользовательских исключений

Реализованы следующие исключения:

* `ResourceNotFoundException`
* `WrongRequestException`
* `InternalServerException`

Каждое исключение наследуется от `RuntimeException` и аннотируется `@ResponseStatus`
для указания HTTP-статуса по умолчанию.

### ResourceNotFoundException (404)

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### WrongRequestException (400)

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongRequestException extends RuntimeException {
    
    public WrongRequestException(String message) {
        super(message);
    }
}
```

### InternalServerException (500)

```java
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends RuntimeException {
    
    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## Стандартизированный формат ошибок

### ApiError

ApiError - это стандартизированный класс для представления информации об ошибках в REST API. 
Он обеспечивает единый формат ответов при возникновении исключительных ситуаций, что улучшает 
согласованность API и упрощает обработку ошибок на стороне клиента.

```java
public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<ValidationError> validationErrors;

    //...
}
```

### Примеры ответов с ошибками

#### [Ошибка 404](https://ru.wikipedia.org/wiki/Список_кодов_состояния_HTTP#404) - Ресурс не найден:

```json
{
    "timestamp": "2025-10-10T16:36:02.95536",
    "status": 404,
    "error": "Resource Not Found",
    "message": "Task not found with id: 999",
    "path": "/api/tasks/999"
}
```

#### [Ошибка 400](https://ru.wikipedia.org/wiki/Список_кодов_состояния_HTTP#400) - Валидация не пройдена:

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

#### [Ошибка 500](https://ru.wikipedia.org/wiki/Список_кодов_состояния_HTTP#500) - Внутренняя ошибка сервера:

```json
{
  "timestamp": "2025-10-10T16:36:02.95536",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/tasks"
}
```

## Рекомендации по проектированию

### 1. Логируйте исключения

Логирование на разных уровнях позволяет быстрее производить диагностику приложения и находить проблемы.

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
    log.warn("Resource not found: {}", e.getMessage());
}

@ExceptionHandler(Exception.class)  
public ResponseEntity<ApiError> handleAllUncaughtException(Exception e, WebRequest request) {
    log.error("Unexpected error occurred: ", e);
}
```

### 2. Сохранение контекста запроса

Извлечение пути запроса и включение его в ответ об ошибке позволяет точно идентифицировать проблемную конечную 
точку.

```java
private String getRequestPath(WebRequest request) {
    if (request instanceof ServletWebRequest) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
    return null;
}
```

### 3. Группируйте ошибки валидации

За счёт группировки клиент будет получать все ошибки валидации сразу, а не по одной.

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiError> handleValidationExceptions(...) {
    List<ApiError.ValidationError> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> new ApiError.ValidationError(
            error.getField(),
            error.getDefaultMessage(), 
            error.getRejectedValue()
        ))
        .collect(Collectors.toList());
}
```

### 4. Обработка ConstraintViolationException

Обработка ошибок валидации на уровне сервиса и репозитория.
Позволяет перехватить и обработать исключения, которые не прошли через правила валидации.

```java
@ExceptionHandler(ConstraintViolationException.class)
public ResponseEntity<ApiError> handleConstraintViolationException(
        ConstraintViolationException ex, WebRequest request) {
    
    List<ApiError.ValidationError> errors = ex.getConstraintViolations()
        .stream()
        .map(violation -> new ApiError.ValidationError(
            violation.getPropertyPath().toString(),
            violation.getMessage(),
            violation.getInvalidValue()
        ))
        .collect(Collectors.toList());
}
```

## Примеры запросов и ответов

### 1. Клиент запрашивает несуществующую задачу

```http
GET /api/tasks/999
```

#### Ответ:

```json
{
    "timestamp": "2025-10-10T16:36:02.95536",
    "status": 404,
    "error": "Resource Not Found", 
    "message": "Task not found with id: 999",
    "path": "/api/tasks/999"
}
```

### 2. Клиент отправляет невалидные данные

```json
POST /api/tasks
Content-Type: application/json

{
    "title": "",
    "description": "Valid description", 
    "status": null
}
```

#### Ответ:

```json
{
    "timestamp": "2025-10-10T16:44:38.5283402",
    "status": 400,
    "error": "Validation Failed",
    "message": "Validation failed for one or more fields",
    "path": "/api/tasks",
    "validationErrors": [
        {
            "field": "status",
            "message": "Status is required",
            "rejectedValue": null
        },
        {
            "field": "title",
            "message": "Title is required",
            "rejectedValue": ""
        },
        {
            "field": "title",
            "message": "Title must be between 1 and 100 characters",
            "rejectedValue": ""
        }
    ]
}
```

<hr>

Единый подход к обработке исключений гарантирует клиентам предсказуемый формат ошибок и понятные сообщения, 
обеспечивая при этом безопасность.