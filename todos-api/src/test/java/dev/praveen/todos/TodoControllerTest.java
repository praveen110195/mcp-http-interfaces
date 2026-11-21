package dev.praveen.todos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(TodoController.class)
@Import(TodoRepository.class)
class TodoControllerTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void findAllReturnsSeedData() {
        assertThat(mvc.get().uri("/api/todos"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.length()").isEqualTo(5);
    }

    @Test
    void findByIdReturnsTodo() {
        assertThat(mvc.get().uri("/api/todos/1"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.title").isEqualTo("Write the Spring Boot 4 talk");
    }

    @Test
    void findByIdReturns404WhenMissing() {
        assertThat(mvc.get().uri("/api/todos/999")).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext // adds a todo, so give the next test fresh seed data
    void createReturns201() {
        assertThat(mvc.post().uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Try HTTP interfaces in an MCP server", "completed": false}
                        """))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.id").isEqualTo(6);
    }

    @Test
    void createRejectsBlankTitle() {
        assertThat(mvc.post().uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "", "completed": false}
                        """))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

}
