package cs209a.finalproject_demo.dataCollection;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class APIClient {

    private final WebClient webClient;


    public APIClient(
            @Value("https://api.stackexchange.com") String baseUrl,
            @Value("${api.key}") String apiKey
    ) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)
                )
                .build();

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public Mono<JsonNode> getQuestionTitlesByIds(List<Long> ids) {
        String idPath = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(";"));

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/{ids}")
                        .queryParam("site", "stackoverflow")
                        .queryParam("filter", "!9_bDDxJY5")
                        .build(idPath)
                )
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    public Mono<JsonNode> getQuestion(long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/{id}")
                        .queryParam("site", "stackoverflow")
                        .build(id))
                .retrieve()
                .bodyToMono(JsonNode.class);
    }


    public Mono<JsonNode> getComments(long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/{id}/comments")
                        .queryParam("site", "stackoverflow")
                        .build(id))
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    public Mono<JsonNode> getTags(String tags) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tags/{tags}/info")
                        .queryParam("site", "stackoverflow")
                        .build(tags))
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    public Mono<JsonNode> getQuestionsByTagAndTime(
            String tag,
            long fromDate,
            long toDate,
            int page
    ) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/2.3/questions")
                        .queryParam("order", "desc")
                        .queryParam("sort", "creation")
                        .queryParam("tagged", tag)
                        .queryParam("fromdate", fromDate)
                        .queryParam("todate", toDate)
                        .queryParam("site", "stackoverflow")
                        .queryParam("page", page)
                        .queryParam("pagesize", 50)
                        .build()
                )
                .retrieve()
                .bodyToMono(JsonNode.class);
    }


    public Mono<JsonNode> getAnswersWithBody(long questionId) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/{id}/answers")
                        .queryParam("site", "stackoverflow")
                        .queryParam("filter", "withbody")
                        .queryParam("pagesize", 100)
                        .build(questionId)
                )
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    public Mono<JsonNode> getCommentsByQuestion(long questionId) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/{id}/comments")
                        .queryParam("site", "stackoverflow")
                        .queryParam("filter", "withbody")
                        .queryParam("pagesize", 100)
                        .build(questionId)
                )
                .retrieve()
                .bodyToMono(JsonNode.class);
    }


}
