package dev.praveen.todos;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An in-memory store, seeded with a few todos so there is something to look at.
 */
@Repository
public class TodoRepository {

    private final Map<Integer, Todo> todos = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public TodoRepository() {
        save(new Todo(null, "Write the Spring Boot 4 talk", true));
        save(new Todo(null, "Answer the HTTP interfaces question", false));
        save(new Todo(null, "Build an MCP server with Spring AI 2", false));
        save(new Todo(null, "Record the follow-up video", false));
        save(new Todo(null, "Drink more coffee", true));
    }

    public List<Todo> findAll() {
        return todos.values().stream()
                .sorted((a, b) -> a.id().compareTo(b.id()))
                .toList();
    }

    public Optional<Todo> findById(Integer id) {
        return Optional.ofNullable(todos.get(id));
    }

    public Todo save(Todo todo) {
        Integer id = todo.id() != null ? todo.id() : nextId.getAndIncrement();
        Todo saved = new Todo(id, todo.title(), todo.completed());
        todos.put(id, saved);
        return saved;
    }

    public boolean existsById(Integer id) {
        return todos.containsKey(id);
    }

    public void deleteById(Integer id) {
        todos.remove(id);
    }

}
