package cs209a.finalproject_demo.dataCollection;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class APIClient {

    private final WebClient webClient;


    public APIClient(
            @Value("${api.base-url}") String baseUrl,
            @Value("${api.key}") String apiKey
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public Mono<JsonNode> getQuestion(long id) {
        return webClient.get()
                .uri("/questions/{id}", id)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    public Mono<JsonNode> getAnswers(long id) {
        return webClient.get()
                .uri("/questions/{id}/answers", id)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    public Mono<JsonNode> getComments(long id) {
        return webClient.get()
                .uri("/questions/{id}/comments", id)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    public Mono<JsonNode> getTags(String tags) {
        return webClient.get()
                .uri("/tags/{tags}/info", tags)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
}
