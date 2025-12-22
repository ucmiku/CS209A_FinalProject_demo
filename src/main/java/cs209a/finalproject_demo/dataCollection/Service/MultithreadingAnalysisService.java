package cs209a.finalproject_demo.dataCollection.Service;

import cs209a.finalproject_demo.DataAnalysis.Repository.MultithreadingAnalysisRepository;
import cs209a.finalproject_demo.DataAnalysis.dto.IssueCategory;
import cs209a.finalproject_demo.DataAnalysis.dto.MultithreadingIssue;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MultithreadingAnalysisService {

    private final MultithreadingAnalysisRepository repository;

    public MultithreadingAnalysisService(MultithreadingAnalysisRepository repository) {
        this.repository = repository;
    }

    /**
     * 分析多线程常见陷阱，返回Top N问题类别
     */
    public List<MultithreadingIssue> analyzeMultithreadingPitfalls(
            LocalDate start, LocalDate end, int topN) {

        List<Map<String, Object>> results = repository.countIssueCategories(start, end);
        long total = results.stream()
                .mapToLong(r -> ((Number) r.get("count")).longValue())
                .sum();

        List<MultithreadingIssue> issues = new ArrayList<>();
        for (Map<String, Object> row : results) {
            String categoryStr = (String) row.get("issue_category");
            long count = ((Number) row.get("count")).longValue();
            String exampleIds = (String) row.get("example_ids");

            IssueCategory category;
            try {
                category = IssueCategory.valueOf(categoryStr);
            } catch (IllegalArgumentException e) {
                category = IssueCategory.GENERAL_MULTITHREADING;
            }

            double percentage = total > 0 ? (count * 100.0 / total) : 0.0;

            issues.add(new MultithreadingIssue(
                    category,
                    count,
                    Math.round(percentage * 100.0) / 100.0, // 保留两位小数
                    exampleIds != null ?
                            (exampleIds.length() > 100 ? exampleIds.substring(0, 100) + "..." : exampleIds) :
                            ""
            ));
        }

        // 按出现次数排序，取Top N
        return issues.stream()
                .sorted((a, b) -> Long.compare(b.occurrenceCount(), a.occurrenceCount()))
                .limit(topN)
                .toList();
    }

    /**
     * 获取多线程相关问题的详细列表
     */
    public List<Map<String, Object>> getMultithreadingQuestions(
            LocalDate start, LocalDate end) {
        return repository.findMultithreadingQuestions(start, end);
    }
}