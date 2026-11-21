package dev.praveen.todos.mcp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TodoClient is just an interface, so the tools can be tested with a plain fake.
 * No HTTP and no running todos API needed.
 */
class TodoToolsTest {

    private FakeTodoClient client;
    private TodoTools tools;

    @BeforeEach
    void setUp() {
        client = new FakeTodoClient();
        client.create(new Todo(null, "Write the talk", false));
        tools = new TodoTools(client);
    }

    @Test
    void listTodosReturnsEverything() {
        assertThat(tools.listTodos()).hasSize(1);
    }

    @Test
    void createTodoStartsNotCompleted() {
        Todo created = tools.createTodo("Answer the question");
        assertThat(created.id()).isEqualTo(2);
        assertThat(created.completed()).isFalse();
    }

    @Test
    void completeTodoKeepsTheTitle() {
        Todo completed = tools.completeTodo(1);
        assertThat(completed).isEqualTo(new Todo(1, "Write the talk", true));
    }

    @Test
    void deleteTodoRemovesIt() {
        tools.deleteTodo(1);
        assertThat(tools.listTodos()).isEmpty();
    }

    static class FakeTodoClient implements TodoClient {

        private final List<Todo> todos = new ArrayList<>();

        @Override
        public List<Todo> findAll() {
            return List.copyOf(todos);
        }

        @Override
        public Todo findById(Integer id) {
            return todos.stream().filter(t -> t.id().equals(id)).findFirst().orElseThrow();
        }

        @Override
        public Todo create(Todo todo) {
            Todo saved = new Todo(todos.size() + 1, todo.title(), todo.completed());
            todos.add(saved);
            return saved;
        }

        @Override
        public Todo update(Integer id, Todo todo) {
            todos.replaceAll(t -> t.id().equals(id) ? todo : t);
            return todo;
        }

        @Override
        public void delete(Integer id) {
            todos.removeIf(t -> t.id().equals(id));
        }

    }

}
