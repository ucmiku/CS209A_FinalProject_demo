package cs209a.finalproject_demo.DataAnalysis.dto;

public record SolvabilityFactor(
        String factorName,         // 因素名称
        double solvableValue,      // 可解答组的值
        double unsolvableValue,    // 难解答组的值
        double difference,         // 差值
        String unit                // 单位/说明
) {}