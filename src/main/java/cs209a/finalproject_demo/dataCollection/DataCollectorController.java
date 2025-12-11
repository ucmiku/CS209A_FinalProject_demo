package cs209a.finalproject_demo.dataCollection;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/collector")
public class DataCollectorController {
    private final DataColletorService dataColletorService;

    public DataCollectorController(DataColletorService dataColletorService) {
        this.dataColletorService = dataColletorService;
    }

    @GetMapping("/question/{id}")
    public Mono<QuestionThread> collect(@PathVariable long id) {
        return dataColletorService.collect(id);
    }
}
