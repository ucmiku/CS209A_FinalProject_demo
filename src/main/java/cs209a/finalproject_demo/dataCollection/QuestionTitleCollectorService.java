package cs209a.finalproject_demo.dataCollection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class QuestionTitleCollectorService {

    private static final int BATCH_SIZE = 50;

    private final QuestionRepository questionRepository;
    private final QuestionTitleRepository titleRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;
    private final APIClient apiClient;
    private final ObjectMapper objectMapper;

    public QuestionTitleCollectorService(
            QuestionRepository questionRepository,
            QuestionTitleRepository titleRepository, AnswerRepository answerRepository, CommentRepository commentRepository,
            APIClient apiClient,
            ObjectMapper objectMapper
    ) {
        this.questionRepository = questionRepository;
        this.titleRepository = titleRepository;
        this.answerRepository = answerRepository;
        this.commentRepository = commentRepository;
        this.apiClient = apiClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Mono<Void> collectAnswersFromTitles() {

        List<Long> questionIds = titleRepository.findAllQuestionIds();

        return Flux.fromIterable(questionIds)
                .flatMap(this::fetchAndSaveAnswers, 3) // 并发度别太高
                .then();
    }

    private Mono<Void> fetchAndSaveAnswers(Long questionId) {

        return apiClient.getAnswersWithBody(questionId)
                .doOnNext(json -> {
                    JsonNode items = json.path("items");

                    for (JsonNode node : items) {
                        Answer a = new Answer();
                        a.setAnswerId(node.path("answer_id").asLong());
                        a.setQuestionId(questionId);
                        a.setScore(node.path("score").asInt());
                        a.setAccepted(node.path("is_accepted").asBoolean(false));
                        a.setBody(node.path("body").asText());

                        answerRepository.save(a);
                    }
                })
                .onErrorResume(e -> {
                    return Mono.empty();
                })
                .then();
    }

    @Transactional
    public Mono<Void> collectCommentsFromTitles() {

        List<Long> questionIds = titleRepository.findAllQuestionIds();

        return Flux.fromIterable(questionIds)
                .flatMap(this::fetchAndSaveComments, 3)
                .then();
    }

    private Mono<Void> fetchAndSaveComments(Long questionId) {

        return apiClient.getCommentsByQuestion(questionId)
                .doOnNext(json -> {
                    JsonNode items = json.path("items");

                    for (JsonNode node : items) {
                        Comment c = new Comment();
                        c.setCommentId(node.path("comment_id").asLong());
                        c.setQuestionId(questionId);
                        c.setScore(node.path("score").asInt());
                        c.setBody(node.path("body").asText());

                        commentRepository.save(c);
                    }
                })
                .onErrorResume(e -> {
                    return Mono.empty();
                })
                .then();
    }



    public Mono<Void> collectTitles() {

        List<Long> allIds = questionRepository.findAll()
                .stream()
                .map(Question::getQuestionId)
                .toList();

        List<List<Long>> batches = partition(allIds, BATCH_SIZE);

        return Flux.fromIterable(batches)
                .concatMap(this::fetchAndSaveBatch)
                .then();
    }

    private Mono<Void> fetchAndSaveBatch(List<Long> batch) {
        return apiClient.getQuestionTitlesByIds(batch)
                .flatMap(json -> {
                    JsonNode items = json.path("items");

                    return Flux.fromIterable(items)
                            .map(node -> {
                                Long qid = node.path("question_id").asLong();
                                String title = node.path("title").asText();

                                QuestionTitle qt = new QuestionTitle();
                                qt.setQuestionId(qid);
                                qt.setTitle(title);

                                return qt;
                            })
                            .doOnNext(titleRepository::save)
                            .then();
                })
                .onErrorResume(e -> {
                    return Mono.empty();
                });
    }


    private List<List<Long>> partition(List<Long> ids, int size) {
        List<List<Long>> result = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += size) {
            result.add(ids.subList(i, Math.min(i + size, ids.size())));
        }
        return result;
    }
}

