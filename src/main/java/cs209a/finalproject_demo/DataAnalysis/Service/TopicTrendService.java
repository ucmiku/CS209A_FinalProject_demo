package cs209a.finalproject_demo.DataAnalysis.Service;

import cs209a.finalproject_demo.DataAnalysis.JavaTopics;
import cs209a.finalproject_demo.DataAnalysis.dto.TopicTrend;
import cs209a.finalproject_demo.DataAnalysis.dto.TrendPoint;
import cs209a.finalproject_demo.DataAnalysis.Repository.TopicTrendRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class TopicTrendService {

    private final TopicTrendRepository repository;

    public TopicTrendService(TopicTrendRepository repository) {
        this.repository = repository;
    }

    public TopicTrend getTopicTrend(
            JavaTopics topic, LocalDate start, LocalDate end) {

        Map<String, TrendPoint> monthly = new TreeMap<>();

        // 1️⃣ Question trends
        for (var row : repository.queryQuestionTrend(topic.getTags(), start, end)) {

            String month = row.get("month").toString().substring(0, 7);
            long qCnt = ((Number) row.get("question_count")).longValue();
            double avgScore = row.get("avg_score") == null ? 0.0
                    : ((Number) row.get("avg_score")).doubleValue();

            monthly.merge(
                    month,
                    new TrendPoint(month, qCnt, 0, avgScore),
                    (oldV, newV) -> new TrendPoint(
                            month,
                            oldV.questionCount() + newV.questionCount(),
                            oldV.answerCount(),
                            // 简化平均（报告可说明）
                            (oldV.avgScore() + newV.avgScore()) / 2
                    )
            );
        }

        // 2️⃣ Answer trends
        for (var row : repository.queryAnswerTrend(topic.getTags(), start, end)) {

            String month = row.get("month").toString().substring(0, 7);
            long aCnt = ((Number) row.get("answer_count")).longValue();

            monthly.merge(
                    month,
                    new TrendPoint(month, 0, aCnt, 0),
                    (oldV, newV) -> new TrendPoint(
                            month,
                            oldV.questionCount(),
                            oldV.answerCount() + newV.answerCount(),
                            oldV.avgScore()
                    )
            );
        }

        return new TopicTrend(topic, new ArrayList<>(monthly.values()));
    }
}
