package cs209a.finalproject_demo.DataAnalysis.dto;

import java.time.LocalDateTime;
public record SolvabilityMetric(
        Long questionId,
        boolean isSolvable,
        boolean hasCodeSnippet,
        int descriptionLength,
        int tagCount,
        int answerCount,
        boolean hasAcceptedAnswer,
        int maxAnswerScore,
        LocalDateTime creationDate
) {}

