package cs209a.finalproject_demo.DataAnalysis.Controller;

import cs209a.finalproject_demo.DataAnalysis.JavaTopics;
import cs209a.finalproject_demo.DataAnalysis.dto.TopicTrend;
import cs209a.finalproject_demo.DataAnalysis.Service.TopicTrendService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class TopicTrendController {

    private final TopicTrendService service;

    public TopicTrendController(TopicTrendService service) {
        this.service = service;
    }

    @GetMapping("/topic-trends")
    public List<TopicTrend> getTopicTrends(
            @RequestParam List<JavaTopics> topics,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return topics.stream()
                .map(t -> service.getTopicTrend(t, start, end))
                .toList();
    }
}
