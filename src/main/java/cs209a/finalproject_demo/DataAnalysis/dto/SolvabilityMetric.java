package cs209a.finalproject_demo.DataAnalysis.dto;

import java.time.LocalDateTime;

public record SolvabilityMetric(
        Long questionId,
        boolean isSolvable,        // 是否可解答
        boolean hasCodeSnippet,    // 是否有代码片段
        int descriptionLength,     // 描述长度
        int tagCount,              // 标签数量
        int answerCount,           // 答案数量
        boolean hasAcceptedAnswer, // 有无接受答案
        int maxAnswerScore,        // 最高答案得分
        LocalDateTime creationDate // 创建时间（用于分析时效性）
) {}