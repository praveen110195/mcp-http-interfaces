package dev.praveen.todos.mcp;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(String path) {
        super("No todo found at " + path);
    }

}
