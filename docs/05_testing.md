# 🧪 Тестирование

Тестирование — это проверка системы на соответствие требованиям и выявление ошибок в её работе.

В проекте используется комбинированный подход к тестированию, обеспечивающий высокое
качество кода через разные уровни тестового покрытия.

### Структура тестов в проекте:

* **`TaskControllerTest`**
* **`TaskServiceTest`**
* **`TaskManagerApplicationTests`**

## Unit-тесты сервисов

### Тестирование TaskService

Unit-тесты фокусируются на бизнес-логике без зависимостей от внешних систем.

`TaskServiceTest` - это unit-тест для сервисного слоя приложения,
построенный с использованием фреймворка Mockito.

**Структура `TaskServiceTest`**

|   Аннотация   |                                                                     Описание                                                                     |
|:-------------:|:------------------------------------------------------------------------------------------------------------------------------------------------:|
| `@ExtendWith` |                                                  Используется для интеграции Mockito в JUnit 5                                                   |
|    `@Mock`    | Создает [mock-объекты](https://ru.wikipedia.org/wiki/Mock-%D0%BE%D0%B1%D1%8A%D0%B5%D0%BA%D1%82 ) зависимостей (`TaskRepository` и `TaskMapper` ) |
| `@BeforeEach` |                                       Метод выполняется перед каждым тестом для инициализации общих данных                                       |
|    `@Test`    |                                                        Помечает метод как тестовый случай                                                        |

**Принцип работы `TaskServiceTest`**

1. Класс использует аннотацию `@ExtendWith(MockitoExtension.class)` для интеграции [Mockito](https://site.mockito.org/) с [JUnit 5](https://docs.junit.org/current/user-guide/#overview-what-is-junit), 
что позволяет автоматически создавать и внедрять mock-объекты. В качестве зависимостей сервиса 
создаются mock-версии `TaskRepository` и `TaskMapper`, которые затем внедряются в тестируемый сервис 
`TaskServiceImpl` через аннотацию `@InjectMocks`.

2. В методе `setUp()`, выполняемом перед каждым тестом, инициализируются тестовые данные: создаётся 
объект сущности `Task` и соответствующий ему DTO `TaskResponse` с одинаковыми идентификаторами и заголовками.

```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @InjectMocks
    private TaskServiceImpl taskService;
    private Task testTask;
    private TaskResponse testTaskResponse;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTaskResponse = new TaskResponse(1L, "Test Task", null, null, null);
    }

    @Test
    void testGetTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskMapper.entityToDTO(testTask)).thenReturn(testTaskResponse);
        TaskResponse result = taskService.getTask(1L);
        assertEquals(1L, result.id());
        assertEquals("Test Task", result.title());
        verify(taskRepository, times(1)).findById(1L);
    }
}
```

### Тестирование методов

Ниже представлен пример теста `testGetTaskById_NotFound()`. Он проверяет ситуацию, когда задача с указанным идентификатором не существует:

1. Тест определяет mock-репозиторий так, чтобы он возвращал пустой [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) при поиске задачи с ID 999, 
имитируя отсутствие записи в базе данных.

2. Затем проверяется, что при вызове метода `getTask()` сервиса 
действительно выбрасывается исключение `ResourceNotFoundException`, что соответствует ожидаемому 
поведению при попытке доступа к несуществующему ресурсу.

3. Также проверяется, что метод репозитория `findById()` был вызван ровно один раз с корректным 
идентификатором, обеспечивая проверку корректного взаимодействия между сервисом и репозиторием.

```java
@Test
void testGetTaskById_NotFound() {
    when(taskRepository.findById(999L)).thenReturn(Optional.empty());
    
    assertThrows(ResourceNotFoundException.class, () -> {
        taskService.getTask(999L);
    });
    
    verify(taskRepository, times(1)).findById(999L);
}
```

## Интеграционные тесты контроллеров

### Тестирование TaskController

Интеграционные тесты проверяют взаимодействие между контроллером и HTTP-слоем. Примером такого теста является `TaskControllerTest`.

Класс `TaskControllerTest` представляет собой интеграционный тест для веб-слоя приложения, 
использующий аннотацию `@WebMvcTest` для фокусировки только на MVC-компонентах.

Spring автоматически настраивает тестовый контекст, предоставляя [MockMvc](https://docs.spring.io/spring-framework/reference/testing/mockmvc.html) для выполнения HTTP-запросов 
и [ObjectMapper]https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-databind/latest/com/fasterxml/jackson/databind/ObjectMapper.html) для преобразования объектов в JSON. Зависимость `TaskService` заменяется на `@MockBean`, 
что позволяет контролировать его поведение без использования настоящей реализации.

Тест проверяет корректность работы конечной точки для создания задач, отправляя POST-запрос с данными 
задачи и проверяя HTTP-статус ответа, структуру возвращаемого JSON и тот факт, что соответствующий метод сервиса был вызван.

**Структура `TaskServiceTest`**

|   Аннотация    |                                            Описание                                            |
|:--------------:|:----------------------------------------------------------------------------------------------:|
| `@WebMvcTest`  |                             Позволяет тестировать только MVC-слоя                              |
|  `@Autowired`  | Используется для [внедрения зависимостей](https://ru.wikipedia.org/wiki/Внедрение_зависимости) |
| `@MockitoBean` |                          Создает mock-версию бина в Spring контексте                           |
|    `@Test`     |                                  Помечает метод как тестовый                                   |

**Пример кода `TaskControllerTest`**

```java
@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TaskService taskService;

    @Test
    void testCreateTask() throws Exception {
        TaskRequest request = new TaskRequest("Test Task", "Test Description", TaskStatus.TODO);
        TaskResponse response = new TaskResponse(1L, "Test Task", "Test Description", TaskStatus.TODO, LocalDateTime.now());
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(response);
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"));
        verify(taskService, times(1)).createTask(any(TaskRequest.class));
    }
}
```

### Тестирование валидации в контроллере

Тест `testCreateTaskWithEmptyTitle()` проверяет три ключевых этапа обработки невалидных данных в контроллере:

1. Происходит отправка POST-запроса с пустым заголовком задачи. Далее тест ожидает ответ со статусом 
`400 Bad Request`.
2. Затем тест проверяет, что в ответе присутствует массив `validationErrors` с информацией о поле
`title`, что подтверждает корректную работу аннотаций валидации.
3. Тест должен подтвердить, что при наличии ошибок валидации сервисный слой не вызывается, предотвращая
обработку некорректных данных на уровне бизнес-логики.

#### Пример кода

```java
@Test
void testCreateTaskWithEmptyTitle() throws Exception {
    TaskRequest invalidRequest = new TaskRequest("", "Test Description", TaskStatus.TODO);
    
    mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.validationErrors[0].field").value("title"));

    verify(taskService, never()).createTask(any());
}
```

## Рекомендации по проектированию

### 1. Правильные именования тестов

Используйте четкое описание методов:

```java
@Test
void testCreateTaskWithEmptyTitle() {}
```

### 2. Паттерн Arrange-Act-Assert

Используйте структуру Arrange-Act-Assert. Благодаря такому шаблону код будет 
явно разделён на следующие этапы:

1. Arrange - подготовка входных данных
2. Act - тестирование целевого метода
3. Assert - проверка результатов

```java
@Test
void createTask_WithValidData_ShouldReturnCreatedTask() {
    // Arrange
    TaskRequest request = new TaskRequest("Title", "Desc", TaskStatus.TODO);
    TaskResponse expectedResponse = new TaskResponse(1L, "Title", "Desc", TaskStatus.TODO, null);
    when(taskService.createTask(any())).thenReturn(expectedResponse);

    // Act
    TaskResponse result = taskService.createTask(request);

    // Assert
    assertNotNull(result);
    assertEquals("Title", result.title());
    verify(taskService, times(1)).createTask(request);
}
```

### 3. Изоляция тестов

1. Создание нового экземпляра сервиса перед каждым тестом гарантирует, что у каждого теста будет 
собственный, чистый экземпляр `TaskServiceImpl` без какого-либо внутреннего состояния от предыдущих тестов.
2. Создание нового объекта `testTask` для каждого теста предотвращает случайное изменение данных между 
тестами и обеспечивает согласованное начальное состояние.
3. Тесты `test1()` и `test2()` работают с разными экземплярами сервиса и разными объектами данных, что полностью исключает их взаимное влияние.

```java
@BeforeEach
void setUp() {
    taskService = new TaskServiceImpl(taskRepository, taskMapper);
    testTask = new Task();
    testTask.setId(1L);
    testTask.setTitle("Test Task");
}

@Test
void test1() {}

@Test  
void test2() {}
```

## Запуск тестов

### Все тесты

```bash
./mvnw test
```

### Только unit-тесты

```bash
./mvnw test -Dtest=*ServiceTest
```

### Только интеграционные тесты
```java
./mvnw test -Dtest=*ControllerTest
```

<hr>

<footer class="footer-nav">
  <a href="../04_validation/" class="footer-nav__link footer-nav__link--prev">
    <span class="footer-nav__icon">←</span>
    <span class="footer-nav__title">Валидация данных</span>
  </a>

  <a href="../06_api_reference/" class="footer-nav__link footer-nav__link--next">
    <span class="footer-nav__title">Справочник API</span>
    <span class="footer-nav__icon">→</span>
  </a>
</footer>