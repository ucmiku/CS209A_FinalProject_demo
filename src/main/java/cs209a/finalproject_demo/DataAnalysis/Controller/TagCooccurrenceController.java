package cs209a.finalproject_demo.DataAnalysis.Controller;

import cs209a.finalproject_demo.DataAnalysis.Service.TagCooccurrenceService;
import cs209a.finalproject_demo.DataAnalysis.dto.TagPairCount;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class TagCooccurrenceController {

    private final TagCooccurrenceService service;

    public TagCooccurrenceController(TagCooccurrenceService service) {
        this.service = service;
    }

    @GetMapping("/tag-cooccurrence")
    public List<TagPairCount> tagCooccurrence(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(defaultValue = "15") int topN
    ) {
        return service.getTopPairs(start, end, topN);
    }
}
