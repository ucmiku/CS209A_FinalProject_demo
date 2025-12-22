package cs209a.finalproject_demo.DataAnalysis.Controller;

import cs209a.finalproject_demo.dataCollection.Service.MultithreadingAnalysisService;
import cs209a.finalproject_demo.DataAnalysis.dto.MultithreadingIssue;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics/multithreading")
public class MultithreadingAnalysisController {

    private final MultithreadingAnalysisService service;

    public MultithreadingAnalysisController(MultithreadingAnalysisService service) {
        this.service = service;
    }

    /**
     * 获取多线程常见陷阱分析（Top N）
     * GET /api/analytics/multithreading/pitfalls?start=2020-01-01&end=2024-12-31&topN=10
     */
    @GetMapping("/pitfalls")
    public List<MultithreadingIssue> getMultithreadingPitfalls(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(defaultValue = "10") int topN) {

        return service.analyzeMultithreadingPitfalls(start, end, topN);
    }

    /**
     * 获取多线程相关问题的详细列表
     * GET /api/analytics/multithreading/questions?start=2020-01-01&end=2024-12-31
     */
    @GetMapping("/questions")
    public List<Map<String, Object>> getMultithreadingQuestions(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        return service.getMultithreadingQuestions(start, end);
    }

    /**
     * 获取多线程问题的总体统计
     * GET /api/analytics/multithreading/summary?start=2020-01-01&end=2024-12-31
     */
    @GetMapping("/summary")
    public Map<String, Object> getMultithreadingSummary(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        List<MultithreadingIssue> issues = service.analyzeMultithreadingPitfalls(start, end, 20);
        long totalIssues = issues.stream().mapToLong(MultithreadingIssue::occurrenceCount).sum();

        return Map.of(
                "analysis_period", start + " to " + end,
                "total_multithreading_issues", totalIssues,
                "top_issues", issues,
                "most_common_issue", issues.isEmpty() ? "N/A" : issues.get(0).category().getDisplayName(),
                "most_common_count", issues.isEmpty() ? 0 : issues.get(0).occurrenceCount()
        );
    }
}