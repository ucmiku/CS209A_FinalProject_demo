package cs209a.finalproject_demo.DataAnalysis.Repository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class TopicTrendRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TopicTrendRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> queryQuestionTrend(
            Set<String> tags, LocalDate start, LocalDate end) {

        String sql = """
            select
              qt.tag as tag,
              date_trunc('month', q.creation_date) as month,
              count(*) as question_count,
              avg(q.score) as avg_score
            from question q
            join question_tag qt
              on qt.question_id = q.question_id
            where qt.tag in (:tags)
              and q.creation_date >= :start
              and q.creation_date < :end
            group by qt.tag, month
            order by month
        """;

        return jdbcTemplate.queryForList(
                sql,
                Map.of("tags", tags, "start", start, "end", end)
        );
    }

    public List<Map<String, Object>> queryAnswerTrend(
            Set<String> tags, LocalDate start, LocalDate end) {

        String sql = """
            select
              qt.tag as tag,
              date_trunc('month', a.creation_date) as month,
              count(*) as answer_count
            from answer a
            join question_tag qt
              on qt.question_id = a.question_id
            where qt.tag in (:tags)
              and a.creation_date >= :start
              and a.creation_date < :end
            group by qt.tag, month
            order by month
        """;

        return jdbcTemplate.queryForList(
                sql,
                Map.of("tags", tags, "start", start, "end", end)
        );
    }
}

