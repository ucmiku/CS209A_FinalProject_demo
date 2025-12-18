package cs209a.finalproject_demo.dataCollection.controller;

import cs209a.finalproject_demo.dataCollection.Service.QuestionTagService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class QuestionTagController {

    private final QuestionTagService service;

    public QuestionTagController(QuestionTagService service) {
        this.service = service;
    }

    @PostMapping("/build-question-tags")
    public Map<String, Object> build() {
        int cnt = service.buildQuestionTags();
        return Map.of(
                "status", "ok",
                "questionsProcessed", cnt
        );
    }
}

