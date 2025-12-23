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
     * 鑾峰彇鎵€鏈夐棶棰樼殑鍙В绛旀€ф寚鏍?
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
     * 鍒嗘瀽褰卞搷鍙В绛旀€х殑涓昏鍥犵礌
     */
    public List<SolvabilityFactor> analyzeSolvabilityFactors(
            LocalDate start, LocalDate end) {

        List<Map<String, Object>> results = repository.getGroupedMetrics(start, end);

        // 鎻愬彇鍙В绛旂粍鍜岄毦瑙ｇ瓟缁勭殑缁熻鏁版嵁
        Map<String, Double> solvableStats = Map.of();
        Map<String, Double> unsolvableStats = Map.of();

        for (Map<String, Object> row : results) {
            Boolean isSolvable = (Boolean) row.get("is_solvable");
            if (isSolvable == null) {
                continue;
            }
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

        // 1. 浠ｇ爜鐗囨鍥犵礌
        factors.add(new SolvabilityFactor(
                "Code Snippet Presence",
                solvableStats.getOrDefault("code_snippet", 0.0),
                unsolvableStats.getOrDefault("code_snippet", 0.0),
                solvableStats.getOrDefault("code_snippet", 0.0) -
                        unsolvableStats.getOrDefault("code_snippet", 0.0),
                "% of questions with code"
        ));

        // 2. 鎻忚堪闀垮害鍥犵礌
        factors.add(new SolvabilityFactor(
                "Description Length",
                solvableStats.getOrDefault("desc_length", 0.0),
                unsolvableStats.getOrDefault("desc_length", 0.0),
                solvableStats.getOrDefault("desc_length", 0.0) -
                        unsolvableStats.getOrDefault("desc_length", 0.0),
                "characters"
        ));

        // 3. 鏍囩鏁伴噺鍥犵礌
        factors.add(new SolvabilityFactor(
                "Tag Count",
                solvableStats.getOrDefault("tag_count", 0.0),
                unsolvableStats.getOrDefault("tag_count", 0.0),
                solvableStats.getOrDefault("tag_count", 0.0) -
                        unsolvableStats.getOrDefault("tag_count", 0.0),
                "average tags per question"
        ));

        // 4. 绛旀鏁伴噺鍥犵礌锛堟敞鎰忥細杩欏彲鑳芥槸缁撴灉鑰岄潪鍘熷洜锛?
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
     * 鑾峰彇鍙В绛斾笌闅捐В绛旈棶棰樼殑瀵规瘮缁熻
     */
    public Map<String, Object> getSolvabilityComparison(
            LocalDate start, LocalDate end) {

        List<Map<String, Object>> results = repository.getGroupedMetrics(start, end);

        // 璁＄畻鎬绘暟
        long totalQuestions = 0;
        long solvableCount = 0;
        long unsolvableCount = 0;
        long unknownCount = 0;

        for (Map<String, Object> row : results) {
            long count = ((Number) row.get("question_count")).longValue();
            totalQuestions += count;

            Boolean isSolvable = (Boolean) row.get("is_solvable");
            if (Boolean.TRUE.equals(isSolvable)) {
                solvableCount += count;
            } else if (Boolean.FALSE.equals(isSolvable)) {
                unsolvableCount += count;
            } else {
                unknownCount += count;
            }
        }

        double solvablePercentage = totalQuestions > 0 ?
                (solvableCount * 100.0 / totalQuestions) : 0.0;
        double unsolvablePercentage = totalQuestions > 0 ?
                (unsolvableCount * 100.0 / totalQuestions) : 0.0;
        double unknownPercentage = totalQuestions > 0 ?
                (unknownCount * 100.0 / totalQuestions) : 0.0;

        return Map.of(
                "total_questions", totalQuestions,
                "solvable_count", solvableCount,
                "solvable_percentage", Math.round(solvablePercentage * 100.0) / 100.0,
                "unsolvable_count", unsolvableCount,
                "unsolvable_percentage", Math.round(unsolvablePercentage * 100.0) / 100.0,
                "unknown_count", unknownCount,
                "unknown_percentage", Math.round(unknownPercentage * 100.0) / 100.0,
                "analysis_factors", analyzeSolvabilityFactors(start, end)
        );
    }
}
