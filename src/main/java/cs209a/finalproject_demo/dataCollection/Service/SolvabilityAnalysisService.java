package cs209a.finalproject_demo.dataCollection.Service;

import cs209a.finalproject_demo.DataAnalysis.Repository.SolvabilityAnalysisRepository;
import cs209a.finalproject_demo.DataAnalysis.dto.SolvabilityFactor;
import cs209a.finalproject_demo.DataAnalysis.dto.SolvabilityMetric;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SolvabilityAnalysisService {

    private final SolvabilityAnalysisRepository repository;

    public SolvabilityAnalysisService(SolvabilityAnalysisRepository repository) {
        this.repository = repository;
    }

    /**
     * 获取所有问题的可解答性指标
     */
    public List<SolvabilityMetric> getSolvabilityMetrics(
            LocalDate start, LocalDate end) {

        List<Map<String, Object>> results = repository.getSolvabilityMetrics(start, end);
        List<SolvabilityMetric> metrics = new ArrayList<>();

        for (Map<String, Object> row : results) {
            SolvabilityMetric metric = new SolvabilityMetric(
                    ((Number) row.get("question_id")).longValue(),
                    (Boolean) row.get("is_solvable"),
                    (Boolean) row.get("has_code_snippet"),
                    ((Number) row.get("description_length")).intValue(),
                    ((Number) row.get("tag_count")).intValue(),
                    ((Number) row.get("answer_count")).intValue(),
                    (Boolean) row.get("has_accepted_answer"),
                    ((Number) row.get("max_answer_score")).intValue(),
                    ((java.sql.Timestamp) row.get("creation_date")).toLocalDateTime()
            );
            metrics.add(metric);
        }

        return metrics;
    }

    /**
     * 分析影响可解答性的主要因素
     */
    public List<SolvabilityFactor> analyzeSolvabilityFactors(
            LocalDate start, LocalDate end) {

        List<Map<String, Object>> results = repository.getGroupedMetrics(start, end);

        // 提取可解答组和难解答组的统计数据
        Map<String, Double> solvableStats = Map.of();
        Map<String, Double> unsolvableStats = Map.of();

        for (Map<String, Object> row : results) {
            boolean isSolvable = (Boolean) row.get("is_solvable");
            double codeSnippetPct = ((Number) row.get("code_snippet_percentage")).doubleValue();
            double avgDescLength = ((Number) row.get("avg_description_length")).doubleValue();
            double avgTagCount = ((Number) row.get("avg_tag_count")).doubleValue();
            double avgAnswerCount = ((Number) row.get("avg_answer_count")).doubleValue();

            if (isSolvable) {
                solvableStats = Map.of(
                        "code_snippet", codeSnippetPct,
                        "desc_length", avgDescLength,
                        "tag_count", avgTagCount,
                        "answer_count", avgAnswerCount
                );
            } else {
                unsolvableStats = Map.of(
                        "code_snippet", codeSnippetPct,
                        "desc_length", avgDescLength,
                        "tag_count", avgTagCount,
                        "answer_count", avgAnswerCount
                );
            }
        }

        List<SolvabilityFactor> factors = new ArrayList<>();

        // 1. 代码片段因素
        factors.add(new SolvabilityFactor(
                "Code Snippet Presence",
                solvableStats.getOrDefault("code_snippet", 0.0),
                unsolvableStats.getOrDefault("code_snippet", 0.0),
                solvableStats.getOrDefault("code_snippet", 0.0) -
                        unsolvableStats.getOrDefault("code_snippet", 0.0),
                "% of questions with code"
        ));

        // 2. 描述长度因素
        factors.add(new SolvabilityFactor(
                "Description Length",
                solvableStats.getOrDefault("desc_length", 0.0),
                unsolvableStats.getOrDefault("desc_length", 0.0),
                solvableStats.getOrDefault("desc_length", 0.0) -
                        unsolvableStats.getOrDefault("desc_length", 0.0),
                "characters"
        ));

        // 3. 标签数量因素
        factors.add(new SolvabilityFactor(
                "Tag Count",
                solvableStats.getOrDefault("tag_count", 0.0),
                unsolvableStats.getOrDefault("tag_count", 0.0),
                solvableStats.getOrDefault("tag_count", 0.0) -
                        unsolvableStats.getOrDefault("tag_count", 0.0),
                "average tags per question"
        ));

        // 4. 答案数量因素（注意：这可能是结果而非原因）
        factors.add(new SolvabilityFactor(
                "Answer Count (Correlation)",
                solvableStats.getOrDefault("answer_count", 0.0),
                unsolvableStats.getOrDefault("answer_count", 0.0),
                solvableStats.getOrDefault("answer_count", 0.0) -
                        unsolvableStats.getOrDefault("answer_count", 0.0),
                "average answers per question"
        ));

        return factors;
    }

    /**
     * 获取可解答与难解答问题的对比统计
     */
    public Map<String, Object> getSolvabilityComparison(
            LocalDate start, LocalDate end) {

        List<Map<String, Object>> results = repository.getGroupedMetrics(start, end);

        // 计算总数
        long totalQuestions = results.stream()
                .mapToLong(r -> ((Number) r.get("question_count")).longValue())
                .sum();

        // 找出可解答组的数量
        long solvableCount = results.stream()
                .filter(r -> (Boolean) r.get("is_solvable"))
                .mapToLong(r -> ((Number) r.get("question_count")).longValue())
                .sum();

        double solvablePercentage = totalQuestions > 0 ?
                (solvableCount * 100.0 / totalQuestions) : 0.0;

        return Map.of(
                "total_questions", totalQuestions,
                "solvable_count", solvableCount,
                "solvable_percentage", Math.round(solvablePercentage * 100.0) / 100.0,
                "unsolvable_count", totalQuestions - solvableCount,
                "unsolvable_percentage", Math.round((100 - solvablePercentage) * 100.0) / 100.0,
                "analysis_factors", analyzeSolvabilityFactors(start, end)
        );
    }
}