package cs209a.finalproject_demo.DataAnalysis.dto;

public record TrendPoint(
        String month,           // yyyy-MM
        long questionCount,
        long answerCount,
        double avgScore
) {}
