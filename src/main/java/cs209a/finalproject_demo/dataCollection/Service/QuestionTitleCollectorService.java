package cs209a.finalproject_demo.dataCollection.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs209a.finalproject_demo.dataCollection.Attributes.Answer;
import cs209a.finalproject_demo.dataCollection.Attributes.Comment;
import cs209a.finalproject_demo.dataCollection.Attributes.Question;
import cs209a.finalproject_demo.dataCollection.Attributes.QuestionTitle;
import cs209a.finalproject_demo.dataCollection.Repository.AnswerRepository;
import cs209a.finalproject_demo.dataCollection.Repository.CommentRepository;
import cs209a.finalproject_demo.dataCollection.Repository.QuestionRepository;
import cs209a.finalproject_demo.dataCollection.Repository.QuestionTitleRepository;
import cs209a.finalproject_demo.dataCollection.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    public Mono<Void> collectAnswersFromTitles() {

        return Mono.fromCallable(titleRepository::findAllQuestionIds)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::fetchAndSaveAnswers, 3)
                .then();
    }


    private Mono<Void> fetchAndSaveAnswers(Long questionId) {

        return apiClient.getAnswersWithBody(questionId)
                .flatMap(json ->
                        Mono.fromRunnable(() -> saveAnswers(json, questionId))
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .onErrorResume(e -> {
                    return Mono.empty();
                })
                .then();
    }

    @Transactional
    public void saveAnswers(JsonNode json, Long questionId) {

        JsonNode items = json.path("items");

        for (JsonNode node : items) {
            Answer a = new Answer();
            a.setAnswerId(node.path("answer_id").asLong());
            a.setQuestionId(questionId);
            a.setScore(node.path("score").asInt());
            a.setAccepted(node.path("is_accepted").asBoolean(false));
            a.setBody(node.path("body").asText());
            long epoch = node.path("creation_date").asLong();
            a.setCreationDate(
                    LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC)
            );

            answerRepository.save(a);
        }
    }


    public Mono<Void> collectCommentsFromTitles() {

        return Mono.fromCallable(titleRepository::findAllQuestionIds)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .filter(id -> id >= 79777766)
                .flatMap(this::fetchAndSaveComments, 3)
                .then();
    }


    private Mono<Void> fetchAndSaveComments(Long questionId) {

        return apiClient.getCommentsByQuestion(questionId)
                .flatMap(json ->
                        Mono.fromRunnable(() -> saveComments(json, questionId))
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .onErrorResume(e -> {
                    return Mono.empty();
                })
                .then();
    }

    @Transactional
    public void saveComments(JsonNode json, Long questionId) {

        JsonNode items = json.path("items");

        for (JsonNode node : items) {
            Comment c = new Comment();
            c.setCommentId(node.path("comment_id").asLong());
            c.setQuestionId(questionId);
            c.setScore(node.path("score").asInt());
            c.setBody(node.path("body").asText());

            commentRepository.save(c);
        }
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
                                String body = node.path("body").asText();

                                QuestionTitle qt = new QuestionTitle();
                                qt.setQuestionId(qid);
                                qt.setTitle(title);
                                qt.setBody(body);

                                return qt;
                            })
                            .doOnNext(titleRepository::save)
                            .then();
                })
                .onErrorResume(e -> Mono.empty());
    }



    private List<List<Long>> partition(List<Long> ids, int size) {
        List<List<Long>> result = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += size) {
            result.add(ids.subList(i, Math.min(i + size, ids.size())));
        }
        return result;
    }
}

