package cs209a.finalproject_demo.DataAnalysis.Controller;

import cs209a.finalproject_demo.dataCollection.Service.SolvabilityAnalysisService;
import cs209a.finalproject_demo.DataAnalysis.dto.SolvabilityFactor;
import cs209a.finalproject_demo.DataAnalysis.dto.SolvabilityMetric;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics/solvability")
public class SolvabilityAnalysisController {

    private final SolvabilityAnalysisService service;

    public SolvabilityAnalysisController(SolvabilityAnalysisService service) {
        this.service = service;
    }

    /**
     * 获取可解答性指标数据
     * GET /api/analytics/solvability/metrics?start=2020-01-01&end=2024-12-31
     */
    @GetMapping("/metrics")
    public List<SolvabilityMetric> getSolvabilityMetrics(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        return service.getSolvabilityMetrics(start, end);
    }

    /**
     * 分析影响可解答性的因素
     * GET /api/analytics/solvability/factors?start=2020-01-01&end=2024-12-31
     */
    @GetMapping("/factors")
    public List<SolvabilityFactor> analyzeSolvabilityFactors(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        return service.analyzeSolvabilityFactors(start, end);
    }

    /**
     * 获取可解答与难解答问题的对比分析
     * GET /api/analytics/solvability/comparison?start=2020-01-01&end=2024-12-31
     */
    @GetMapping("/comparison")
    public Map<String, Object> getSolvabilityComparison(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        return service.getSolvabilityComparison(start, end);
    }

    /**
     * 获取可解答性分析报告
     * GET /api/analytics/solvability/report?start=2020-01-01&end=2024-12-31
     */
    @GetMapping("/report")
    public Map<String, Object> getSolvabilityReport(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        List<SolvabilityFactor> factors = service.analyzeSolvabilityFactors(start, end);
        Map<String, Object> comparison = service.getSolvabilityComparison(start, end);

        // 找出影响最大的因素（按差值绝对值排序）
        SolvabilityFactor mostInfluential = factors.stream()
                .max((a, b) -> Double.compare(Math.abs(a.difference()), Math.abs(b.difference())))
                .orElse(null);

        // 找出最可能提高可解答性的建议
        String recommendation = "";
        if (mostInfluential != null) {
            if (mostInfluential.factorName().contains("Code Snippet")) {
                recommendation = "Adding code snippets can significantly improve solvability (" +
                        Math.abs(mostInfluential.difference()) + "% difference).";
            } else if (mostInfluential.factorName().contains("Description")) {
                recommendation = "Longer, more detailed descriptions are associated with higher solvability (" +
                        Math.abs(mostInfluential.difference()) + " characters difference).";
            } else if (mostInfluential.factorName().contains("Tag")) {
                recommendation = "Questions with fewer, more focused tags tend to be more solvable (" +
                        Math.abs(mostInfluential.difference()) + " tags difference).";
            }
        }

        return Map.of(
                "analysis_period", start + " to " + end,
                "comparison_summary", comparison,
                "key_factors", factors,
                "most_influential_factor", mostInfluential != null ?
                        Map.of(
                                "factor", mostInfluential.factorName(),
                                "difference", mostInfluential.difference(),
                                "unit", mostInfluential.unit()
                        ) : "No data",
                "recommendation", recommendation,
                "interpretation", "Positive difference values indicate that solvable questions have higher values for that factor."
        );
    }
}