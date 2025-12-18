package cs209a.finalproject_demo.DataAnalysis.dto;

import cs209a.finalproject_demo.DataAnalysis.JavaTopics;
import java.util.List;

public record TopicTrend(
        JavaTopics topic,
        List<TrendPoint> points
) {}
