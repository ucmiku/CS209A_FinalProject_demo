package cs209a.finalproject_demo.DataAnalysis.dto;

public record MultithreadingIssue(
        IssueCategory category,    // 问题类别
        long occurrenceCount,      // 出现次数
        double percentage,         // 占比百分比
        String exampleQuestionIds  // 示例问题ID（便于查看）
) {}