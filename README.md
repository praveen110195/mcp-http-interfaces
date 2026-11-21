# HTTP Interfaces in an MCP Server

Can you use Spring HTTP interfaces inside an MCP server? Yes. This repo has two Spring Boot apps. One is a small todos REST API. The other is a Spring AI MCP server that calls that API through a declarative HTTP interface (`@HttpExchange`), so the MCP tools never touch `RestClient` directly.

## Tested with

Java 25, Spring Boot 4.1.2, Spring AI 2.0.2. Built and run on JDK 27.

## What you need

- JDK 25 or newer
- No API keys. The MCP server does not call a model. Your MCP client brings its own.

## Run it

This is a multi-module Maven project. Start the API first, then the MCP server, each in its own terminal.

```bash
./mvnw -pl todos-api spring-boot:run
```

```bash
./mvnw -pl todos-mcp-server spring-boot:run
```

| App | Port | What it is |
|-----|------|------------|
| `todos-api` | 8080 | REST API at `/api/todos`, seeded with 5 todos (in memory, reset on restart) |
| `todos-mcp-server` | 8081 | MCP server over Streamable HTTP at `/mcp` |

## Try it

Check the API:

```bash
curl localhost:8080/api/todos
```

Connect any MCP client to `http://localhost:8081/mcp`. For Claude Code:

```bash
claude mcp add --transport http todos-demo http://localhost:8081/mcp
```

The server exposes 5 tools:

| Tool | HTTP call it makes |
|------|--------------------|
| `list_todos` | `GET /api/todos` |
| `get_todo` | `GET /api/todos/{id}` |
| `create_todo` | `POST /api/todos` |
| `complete_todo` | `GET` then `PUT /api/todos/{id}` |
| `delete_todo` | `DELETE /api/todos/{id}` |

Then ask your client something like this:

```text
What's on my todo list? Mark "Answer the HTTP interfaces question" as done and add a todo to record the follow-up video.
```

Run `curl localhost:8080/api/todos` again and you'll see the changes in the API.

## Where to look

- [TodoClient.java](todos-mcp-server/src/main/java/dev/danvega/todos/mcp/TodoClient.java) is the HTTP interface. Spring creates the implementation.
- [HttpClientConfig.java](todos-mcp-server/src/main/java/dev/danvega/todos/mcp/HttpClientConfig.java) registers it with `@ImportHttpServices` in a group named `todos`. It also turns a 404 into a clear message for the model.
- [TodoTools.java](todos-mcp-server/src/main/java/dev/danvega/todos/mcp/TodoTools.java) has the `@McpTool` methods. Each one calls `TodoClient`.

The base URL lives in [application.yaml](todos-mcp-server/src/main/resources/application.yaml):

```yaml
spring:
  http:
    serviceclient:
      todos:
        base-url: http://localhost:8080
```

## Branches

| Branch | What it has |
|--------|-------------|
| `start` | The finished todos API and an MCP server with no tools. Start here to build it yourself. |
| `main` | The finished code: the HTTP interface, the MCP tools, the 404 handling and the tests. |

## Good to know

- Spring Boot 4 auto-configures HTTP service groups, but only with a client starter on the classpath. The MCP server adds `spring-boot-starter-restclient` for that.
- Because `TodoClient` is an interface, [TodoToolsTest](todos-mcp-server/src/test/java/dev/danvega/todos/mcp/TodoToolsTest.java) tests the tools with a plain fake. No HTTP and no running API.
- The MCP server has its own `Todo` record. The two apps share no code, only the JSON shape.
- In Spring AI 2.0 the MCP annotations live in `org.springframework.ai.mcp.annotation`. Older examples use `org.springaicommunity.mcp.annotation`.
- When a tool fails, Spring AI prints the error message twice in the tool result. That comes from Spring AI, not this code.
