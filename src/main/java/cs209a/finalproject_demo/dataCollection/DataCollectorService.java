package cs209a.finalproject_demo.dataCollection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class DataCollectorService {

    private final APIClient apiClient;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ObjectMapper objectMapper;

    public DataCollectorService(APIClient apiClient,
                                QuestionRepository questionRepository,
                                AnswerRepository answerRepository,
                                ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> collectQuestionsByTagAndTime(
            String tag,
            LocalDateTime from,
            LocalDateTime to
    ) {
        long fromEpoch = from.toEpochSecond(ZoneOffset.UTC);
        long toEpoch = to.toEpochSecond(ZoneOffset.UTC);

        return Flux
                .range(1, 50)
                .concatMap(page ->
                        apiClient.getQuestionsByTagAndTime(tag, fromEpoch, toEpoch, page)
                                .delayElement(Duration.ofSeconds(1)) // 防 503
                                .doOnNext(json -> {
                                    JsonNode items = json.path("items");
                                    for (JsonNode node : items) {
                                        parseAndSaveQuestion(node);
                                    }
                                })
                )
                .takeUntil(json -> !json.path("has_more").asBoolean())
                .then();
    }





    /*private Mono<Void> collectSingleQuestionWithAnswers(JsonNode questionNode) {
        Question question = parseAndSaveQuestion(questionNode);
        long questionId = question.getQuestionId();

        return apiClient.getAnswersWithBody(questionId)
                .flatMap(answerJson -> saveAnswersWithBody(answerJson, question))
                .then();
    }*/

    private void parseAndSaveQuestion(JsonNode item) {
        long questionId = item.path("question_id").asLong();

        if (questionRepository.existsById(questionId)) {
            return;
        }

        Question question = new Question();
        question.setQuestionId(questionId);
        question.setAnswered(item.path("is_answered").asBoolean());
        question.setAnswerCount(item.path("answer_count").asInt());
        question.setViewCount(item.path("view_count").asInt());
        question.setScore(item.path("score").asInt());

        // tags
        String tags = StreamSupport.stream(
                        item.path("tags").spliterator(), false)
                .map(JsonNode::asText)
                .collect(Collectors.joining(","));
        question.setTags(tags);

        // body（HTML）
        question.setBody(item.path("body").asText());

        questionRepository.save(question);
    }

}
