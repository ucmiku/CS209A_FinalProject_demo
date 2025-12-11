package cs209a.finalproject_demo.dataCollection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DataColletorService {
    private final APIClient apiClient;
    private final ThreadRepository threadRepository;
    private final ObjectMapper objectMapper;


    public DataColletorService(APIClient apiClient, ThreadRepository threadRepository, ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.threadRepository = threadRepository;
        this.objectMapper = objectMapper;
    }

    public Mono<QuestionThread> collect(long questionId) {

        Mono<JsonNode> question = apiClient.getQuestion(questionId);
        Mono<JsonNode> answers = apiClient.getAnswers(questionId);
        Mono<JsonNode> comments = apiClient.getComments(questionId);

        Mono<JsonNode> tags = question.map(q -> {
            JsonNode tagNodes = q.get("items").get(0).get("tags");
            String joined = StreamSupport.stream(tagNodes.spliterator(), false)
                    .map(JsonNode::asText)
                    .collect(Collectors.joining(";"));
            return apiClient.getTags(joined).block();
        });

        return Mono.zip(question, answers, comments, tags)
                .flatMap(tuple -> saveEntry(
                        questionId,
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3(),
                        tuple.getT4()
                ));
    }

    private Mono<QuestionThread> saveEntry(long id,
                                          JsonNode q,
                                          JsonNode a,
                                          JsonNode c,
                                          JsonNode t) {

        QuestionThread entry = new QuestionThread();
        entry.setQuestionId(id);
        entry.setQuestion(q.toString());
        entry.setAnswers(a.toString());
        entry.setComments(c.toString());
        entry.setTags(t.toString());
        entry.setFetchedAt(LocalDateTime.now());

        return Mono.fromCallable(() -> threadRepository.save(entry));
    }
}
