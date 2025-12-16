package cs209a.finalproject_demo.dataCollection;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/collector")
public class DataCollectorController {

    private final DataCollectorService dataCollectorService;
    private final QuestionTitleCollectorService titleService;

    public DataCollectorController(DataCollectorService dataCollectorService, QuestionTitleCollectorService titleCollectorService, QuestionTitleCollectorService titleService) {
        this.dataCollectorService = dataCollectorService;
        this.titleService = titleService;
    }

    @GetMapping("/collect")
    public Mono<String> collect(
            @RequestParam String tag,
            @RequestParam String from,
            @RequestParam String to
    ) {
        return dataCollectorService
                .collectQuestionsByTagAndTime(
                        tag,
                        LocalDateTime.parse(from),
                        LocalDateTime.parse(to)
                )
                .thenReturn("Collection started");
    }

    @GetMapping("/collect-titles")
    public Mono<String> collectTitles() {
        return titleService.collectTitles()
                .thenReturn("Question titles collection started");
    }

    @GetMapping("/collect-answers")
    public ResponseEntity<String> collectAnswers() {
        titleService.collectAnswersFromTitles().subscribe();
        return ResponseEntity.ok("Answer collection started");
    }

    @GetMapping("/collect-comments")
    public ResponseEntity<String> collectComments() {
        titleService.collectCommentsFromTitles().subscribe();
        return ResponseEntity.ok("Comment collection started");
    }

}

