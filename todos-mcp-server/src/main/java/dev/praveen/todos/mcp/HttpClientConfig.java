package dev.praveen.todos.mcp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

/**
 * Registers TodoClient as a bean in the "todos" group.
 * Spring Boot 4 configures the group from spring.http.serviceclient.todos.*
 */
@Configuration(proxyBeanMethods = false)
@ImportHttpServices(group = "todos", types = TodoClient.class)
public class HttpClientConfig {

    /**
     * Optional: customize the RestClient behind the "todos" group.
     * A 404 becomes a short, clear message that the model can read.
     */
    @Bean
    RestClientHttpServiceGroupConfigurer todosGroupConfigurer() {
        return groups -> groups.filterByName("todos").forEachClient((group, builder) ->
                builder.defaultStatusHandler(
                        status -> status.value() == 404,
                        (request, response) -> {
                            throw new TodoNotFoundException(request.getURI().getPath());
                        }));
    }

}
