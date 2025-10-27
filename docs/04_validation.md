# ✅ Валидация данных

## Обзор

Валидация данных - важный аспект создания надежного API. 
Она гарантирует, что входящие данные соответствуют ожидаемому формату
и бизнес-правилам до обработки в бизнес-логике.

## Уровни валидации

### Валидация на уровне DTO

В проекте используется [спецификация Java для валидации данных](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html) с использованием аннотаций:

**Валидация в TaskRequest**

```java
public record TaskRequest(
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    String title,

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,

    @NotNull(message = "Status is required")
    TaskStatus status
) {}
```

### Валидация в контроллере

**Использование аннотации @Valid**

Аннотация `@Valid` автоматически проверяет входящие данные по правилам, заданным в TaskRequest. Если данные прошли 
проверку - то они передаются в параметры метода в виде объекта TaskRequest.

```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest taskRequest) {
        TaskResponse response = taskService.createTask(taskRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest taskRequest) {
        TaskResponse response = taskService.updateTask(id, taskRequest);
        return ResponseEntity.ok(response);
    }
}
```

### Валидация на уровне базы данных

**JPA аннотации в Entity**

```java
@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

## Рекомендации по проектированию

### 1. Эшелонированная защита (Defense in Depth)

Это стратегия, которая, в данном контексте, позволяет проверять данные на разных уровнях 
(dto, сервис, контроллер, бд), что обеспечивает максимальную защиту. Даже если один уровень валидации будет 
пропущен, то следующие уровни не пропустят некорректные данные.

1. Валидация DTO
    ```java
    public record TaskRequest(@NotBlank @Size(max=100) String title) {}
    ```

2. Бизнес-валидация в сервисе
    ```java
    private void validate(TaskRequest request) {
        if (...) {
            throw new WrongRequestException();
        }
    }
    ```
3. Ограничения БД
   ```java
    @Column(nullable=false, length=100)
    private String title;
    ```

### 2. Информативные сообщения об ошибках

```java
@NotBlank(message = "Task title is required")
@Size(min=1, max=100, message = "Title must be between 1 and 100 characters")
```

## Примеры

### Пример некорректного запроса

**Запрос на создание задачи**

```json
POST /api/tasks
Content-Type: application/json
        
{
    "title": "",
    "description": "Description of the new task",
    "status": null
}
```

**Ответ с ошибками валидации**

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

### Пример правильного запроса

**Запрос на создание задачи**

```json
POST /api/tasks HTTP/1.1
Content-Type: application/json

{
    "title": "New task-123",
    "description": "Description of the new task",
    "status": "IN_PROGRESS"
}
```

**Успешный ответ**

```json
HTTP/1.1 200 OK
Content-Type: application/json

{
    "id": 1,
    "title": "New task-123",
    "description": "Description of the new task",
    "status": "IN_PROGRESS",
    "createdAt": "2025-10-10T16:36:02.95536"
}
```

<hr>

Реализованная валидация обеспечивает комплексную защиту данных на всех уровнях приложения - от первичной
проверки DTO до финальных ограничений базы данных. Такой многоуровневый подход гарантирует надежность API
и целостность данных.