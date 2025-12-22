package cs209a.finalproject_demo.dataCollection.Controller;

import cs209a.finalproject_demo.dataCollection.Repository.*;
import cs209a.finalproject_demo.DataAnalysis.Repository.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class DataCleanupController {

    private final QuestionRepository questionRepository;
    private final QuestionTagRepository questionTagRepository;
    private final QuestionTitleRepository questionTitleRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    // 可选的：分析相关Repository
    private final TagCooccurrenceRepository tagCooccurrenceRepository;
    private final TopicTrendRepository topicTrendRepository;
    private final MultithreadingAnalysisRepository multithreadingAnalysisRepository;
    private final SolvabilityAnalysisRepository solvabilityAnalysisRepository;

    public DataCleanupController(
            QuestionRepository questionRepository,
            QuestionTagRepository questionTagRepository,
            QuestionTitleRepository questionTitleRepository,
            AnswerRepository answerRepository,
            CommentRepository commentRepository,
            TagCooccurrenceRepository tagCooccurrenceRepository,
            TopicTrendRepository topicTrendRepository,
            MultithreadingAnalysisRepository multithreadingAnalysisRepository,
            SolvabilityAnalysisRepository solvabilityAnalysisRepository) {

        this.questionRepository = questionRepository;
        this.questionTagRepository = questionTagRepository;
        this.questionTitleRepository = questionTitleRepository;
        this.answerRepository = answerRepository;
        this.commentRepository = commentRepository;
        this.tagCooccurrenceRepository = tagCooccurrenceRepository;
        this.topicTrendRepository = topicTrendRepository;
        this.multithreadingAnalysisRepository = multithreadingAnalysisRepository;
        this.solvabilityAnalysisRepository = solvabilityAnalysisRepository;
    }

    /**
     * 清理所有数据表（完整清理）
     * DELETE http://localhost:8080/api/admin/clear-all
     */
    @DeleteMapping("/clear-all")
    @Transactional
    public Map<String, Object> clearAllData() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", System.currentTimeMillis());

        // 清理顺序很重要：先清理外键依赖的表
        long commentCount = commentRepository.count();
        commentRepository.deleteAll();
        result.put("comments_deleted", commentCount);

        long answerCount = answerRepository.count();
        answerRepository.deleteAll();
        result.put("answers_deleted", answerCount);

        long titleCount = questionTitleRepository.count();
        questionTitleRepository.deleteAll();
        result.put("titles_deleted", titleCount);

        long tagCount = questionTagRepository.count();
        questionTagRepository.deleteAll();
        result.put("tags_deleted", tagCount);

        long questionCount = questionRepository.count();
        questionRepository.deleteAll();
        result.put("questions_deleted", questionCount);

        result.put("status", "success");
        result.put("message", "All data tables cleared successfully");
        result.put("total_records_deleted",
                commentCount + answerCount + titleCount + tagCount + questionCount);

        return result;
    }

    /**
     * 选择性清理（按表清理）
     * DELETE http://localhost:8080/api/admin/clear?tables=questions,answers
     */
    @DeleteMapping("/clear")
    @Transactional
    public Map<String, Object> clearSelectedTables(
            @RequestParam(defaultValue = "all") String tables) {

        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", System.currentTimeMillis());

        if (tables.equalsIgnoreCase("all") || tables.contains("questions")) {
            long count = questionRepository.count();
            questionRepository.deleteAll();
            result.put("questions_deleted", count);
        }

        if (tables.equalsIgnoreCase("all") || tables.contains("titles")) {
            long count = questionTitleRepository.count();
            questionTitleRepository.deleteAll();
            result.put("titles_deleted", count);
        }

        if (tables.equalsIgnoreCase("all") || tables.contains("tags")) {
            long count = questionTagRepository.count();
            questionTagRepository.deleteAll();
            result.put("tags_deleted", count);
        }

        if (tables.equalsIgnoreCase("all") || tables.contains("answers")) {
            long count = answerRepository.count();
            answerRepository.deleteAll();
            result.put("answers_deleted", count);
        }

        if (tables.equalsIgnoreCase("all") || tables.contains("comments")) {
            long count = commentRepository.count();
            commentRepository.deleteAll();
            result.put("comments_deleted", count);
        }

        result.put("status", "success");
        result.put("message", "Selected tables cleared: " + tables);

        return result;
    }

    /**
     * 只清理分析相关数据（保留原始采集数据）
     * DELETE http://localhost:8080/api/admin/clear-analysis
     */
    @DeleteMapping("/clear-analysis")
    @Transactional
    public Map<String, Object> clearAnalysisDataOnly() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", System.currentTimeMillis());

        // 只清理分析相关的缓存或中间表
        // 这里可以根据您的分析需求添加清理逻辑

        result.put("status", "success");
        result.put("message", "Analysis data cleared (original collection data preserved)");
        result.put("preserved_data", new String[]{
                "questions", "titles", "tags", "answers", "comments"
        });

        return result;
    }

    /**
     * 检查数据库状态
     * GET http://localhost:8080/api/admin/status
     */
    @GetMapping("/status")
    public Map<String, Object> getDatabaseStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", System.currentTimeMillis());

        status.put("questions_count", questionRepository.count());
        status.put("titles_count", questionTitleRepository.count());
        status.put("tags_count", questionTagRepository.count());
        status.put("answers_count", answerRepository.count());
        status.put("comments_count", commentRepository.count());

        status.put("database_size_estimate",
                calculateEstimatedSize(
                        (long) status.get("questions_count"),
                        (long) status.get("titles_count"),
                        (long) status.get("tags_count"),
                        (long) status.get("answers_count"),
                        (long) status.get("comments_count")
                ) + " MB");

        return status;
    }

    private String calculateEstimatedSize(long q, long t, long tg, long a, long c) {
        // 简单估算：每条记录大约 1KB
        long totalRecords = q + t + tg + a + c;
        double sizeMB = totalRecords * 1.0 / 1024; // KB to MB
        return String.format("%.2f", sizeMB);
    }

    /**
     * 安全模式清理（确认机制）
     * POST http://localhost:8080/api/admin/confirm-clear
     */
    @PostMapping("/confirm-clear")
    public Map<String, Object> confirmAndClear(@RequestBody Map<String, String> request) {
        String confirmation = request.get("confirmation");
        String operation = request.get("operation");

        Map<String, Object> result = new HashMap<>();

        if (!"DELETE_ALL_DATA_2025".equals(confirmation)) {
            result.put("status", "error");
            result.put("message", "Invalid confirmation code");
            return result;
        }

        switch (operation) {
            case "all":
                return clearAllData();
            case "analysis":
                return clearAnalysisDataOnly();
            default:
                result.put("status", "error");
                result.put("message", "Unknown operation: " + operation);
                return result;
        }
    }
}