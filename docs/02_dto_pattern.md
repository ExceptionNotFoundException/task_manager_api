# 🛡️ Шаблон проектирования DTO

**DTO (Data Transfer Object)** - это объекты, которые используются не только для передачи данных между 
подсистемами приложения, но и служат для изоляции слоя представления - API от слоя доменных моделей - Entity.

### Проблемы при использовании Entity напрямую в API

При использовании сущности (Entity) в контроллере Вы можете столкнуться со следующими проблемами:

* Клиенты смогут передавать в запросе любые поля сущности.
* В ответах клиент видит **все** поля сущности.
* Изменения в модели базы данных автоматически влияют на API.
* Невозможно разделить правила валидации для базы данных и для API.

Предположим, что в сущности `Task` есть поле `someInternalField`, которое не должно быть доступно для модификации и
передачи. Клиент сможет задать своё значение и передать поле `someInternalField`. Также клиент сможет задать `id` вручную.

**Пример сущности `Task`**

```java
@Entity
public class Task {
  private Long id;
  private String title;
  private String description;
  private TaskStatus status;
  private LocalDateTime createdAt;
  private String someInternalField;
  ...
}
```

**Пример работы в контроллере с использованием `Task`**

```java
@RestController
public class TaskController {
    
  @PostMapping("/tasks")
  public Task createTask(@RequestBody Task task) {
    return taskRepository.save(task);
  }
}
```

**Пример запроса**

```json
POST /api/tasks

{
  "id": 5,
  "title": "New Task-123",
  "description": "Example of a new task description",
  "status": "TODO"
  "someInternalField": "any value"
}
```

Решением проблемы безопасности является разделение моделей API и базы данных через DTO. 

### Пример решения с DTO

**Используйте `TaskRequest` вместо `Task`**

```java
public record TaskRequest(String title, String description) {}
```

**Пример работы в контроллере с использованием `TaskRequest`**

```java

@RestController
public class TaskController {
    
    @PostMapping("/tasks")
    public TaskResponse createTask(@RequestBody TaskRequest taskRequest) {
        Task task = taskMapper.dtoToEntity(taskRequest);
        Task savedTask = taskRepository.save(task);
        return taskMapper.entityToDTO(savedTask);
    }
}
```

!!! note "Примечание"
    Здесь код с маппингом сущности и DTO вынесен в контроллер, для примера. В приложении эта часть кода 
    определена в сервисном слое, в классе `TaskServiceImpl`.

**Пример запроса при использовании DTO**

```json
POST /api/tasks

{
  "title": "New Task-123",
  "description": "Example of a new task description",
  "status": "TODO"
}
```

Используйте отдельные классы `TaskRequest` для входящих данных и `TaskResponse` для исходящих, которые содержат только разрешенные для клиента поля.
Для преобразования из Entity в DTO и из DTO в Entity используйте маппер, например библиотеку [MapStruct](https://mapstruct.org/), либо другой.

## Реализация DTO в проекте

### Структура DTO классов

Пакет: `com.example.taskmanager.dto`

Классы:

* `TaskRequest`
* `TaskResponse`
* `ApiError`

!!! note "Примечание"
    Информация о структуре DTO также присутствует в разделе [архитектуры приложения](../01_architecture/#dto)

### TaskRequest - для создания и обновления задач
```java
@Schema(description = "Request for creating or updating a task")
public record TaskRequest(
    @Schema(description = "Task title", example = "Write API documentation")
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    String title,

    @Schema(description = "Task description", example = "Create comprehensive API docs")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,

    @Schema(description = "Task status", example = "IN_PROGRESS")
    @NotNull(message = "Status is required")
    TaskStatus status
) {}
```
#### Особенности TaskRequest

* Record-класс сокращает шаблонный код и делает его неизменяемым, что гарантирует защиту от случайных изменений.
* Аннотации Swagger (`@Schema`)
* Аннотации валидации данных (`@NotBlank`, `@Size`, `@NotNull`)

### TaskResponse - для возврата данных клиенту
```java
@Schema(description = "Task response data")
public record TaskResponse(
    @Schema(description = "Task ID", example = "1")
    Long id,
    
    @Schema(description = "Task title", example = "Write API documentation")
    String title,
    
    @Schema(description = "Task description", example = "Create comprehensive API docs")
    String description,
    
    @Schema(description = "Task status", example = "IN_PROGRESS")
    TaskStatus status,
    
    @Schema(description = "Creation timestamp")
    LocalDateTime createdAt
) {}
```

#### Особенности TaskResponse
* Используется [Record-класс](https://docs.oracle.com/en/java/javase/17/language/records.html) (гарантия неизменяемости)
* Содержит только необходимые для ответа поля

## Маппинг между Entity и DTO

В проекте для наглядности используется ручной маппинг, но в промышленной разработке рекомендуется использовать готовые решения.

**Текущая реализация (ручной маппинг)**
```java
@Component
public class TaskMapper {

    public TaskResponse entityToDTO(Task task) {
        return new TaskResponse(
                task.getId(), 
                task.getTitle(), 
                task.getDescription(), 
                task.getStatus(), 
                task.getCreatedAt());
    }

    public Task dtoToEntity(TaskRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setStatus(taskRequest.status());
        return task;
    }

    public void updateEntityFromDto(TaskRequest taskRequest, Task task) {
        if (taskRequest.title() != null) {
            task.setTitle(taskRequest.title());
        }
        if (taskRequest.description() != null) {
            task.setDescription(taskRequest.description());
        }
        if (taskRequest.status() != null) {
            task.setStatus(taskRequest.status());
        }
    }
}
```

**Альтернатива: MapStruct**
```java
@Mapper(componentModel = "spring")
public interface TaskMapper {
    
    TaskResponse toResponse(Task task);
    
    Task toEntity(TaskRequest request);
}
```

**Преимущества MapStruct:**

* Генерация кода во время компиляции
* Обеспечивает типобезопасность
* Используется меньше повторяющегося кода

## Преимущества DTO паттерна

### 1. Безопасность

Для защиты от несанкционированной инициализации данных рекомендуется использовать DTO только с разрешенными полями. Для этого:

* Создавайте отдельные DTO для каждой конечной точки
* Используйте явное преобразование DTO в Entity вместо прямого использования Entity в контроллерах

### 2. Оптимизация производительности

При преобразовании объекта в JSON его поля с `FetchType.LAZY` могут вызвать ошибку.
Чтобы этого избежать, используйте DTO с явным контролем данных:

```java
public record TaskResponse(Long id, String status) {}
```

!!! note "Примечание"
    `FetchType.LAZY` - это значение параметра `fetch`, которое означает "ленивую загрузку" данных. Это значит, что данные будут
    загружены не сразу, а только тогда, когда это будет необходимо. Параметр `fetch` указывается в аннотациях,
    определяющих отношения между сущностями в базе данных - `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`.
    Также есть другое значение параметра `fetch` - `FetchType.EAGER`. Используя `FetchType.EAGER`, все данные будут
    загружены сразу.

### 3. Валидация данных

Для того чтобы API стал более гибким и соответствовал конкретным бизнес-процессам, разделяйте валидацию по контекстам.
Например, для **создания** задачи будут установлены одни правила валидации, а для **обновления** задачи уже другие:

```java
public record TaskCreateRequest(
    @NotBlank String title,
    @Size(max = 500) String description
) {}

public record TaskUpdateRequest(
    @Size(min = 5, max = 100) String title,
    TaskStatus status
) {}
```

## Рекомендации по проектированию

### 1. Используйте Record для DTO

Вместо обычно класса: 

```java
public record TaskRequest(String title, String description) {}
```

### 2. Разделяйте DTO по ответственности

Для чтения

```java
TaskResponse
```

Для создания

```java
TaskCreateRequest
```

Для обновления

```java
TaskUpdateRequest
```

### 3. Проверяйте данные на уровне DTO

```java
public record TaskRequest(
    @NotBlank String title,
    @NotNull TaskStatus status
) {}
```

### 4. Документируйте

```java
public record TaskResponse(
    @Schema(description = "Task ID", example = "1")
    Long id,
    
    @Schema(description = "Task title", example = "Write a client for an external weather API")
    String title
) {}
```

<hr>

DTO является защитным механизмом, который обеспечивает безопасность, предсказуемость и стабильность API.





