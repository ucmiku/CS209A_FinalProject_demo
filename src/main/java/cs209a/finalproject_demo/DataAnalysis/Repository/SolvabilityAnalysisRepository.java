package cs209a.finalproject_demo.DataAnalysis.Repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class SolvabilityAnalysisRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SolvabilityAnalysisRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 获取可解答性指标数据
     */
    public List<Map<String, Object>> getSolvabilityMetrics(
            LocalDate start, LocalDate end) {

        String sql = """
            SELECT 
                q.question_id,
                q.creation_date,
                q.answer_count,
                q.score as question_score,
                qt.body,
                LENGTH(qt.body) as description_length,
                -- 是否有代码片段
                CASE 
                    WHEN qt.body LIKE '%<code>%' OR qt.body LIKE '%```%' 
                         OR qt.body ~* 'public\\s+class|void\\s+main' 
                    THEN true 
                    ELSE false 
                END as has_code_snippet,
                -- 标签数量
                (SELECT COUNT(*) FROM question_tag qtg WHERE qtg.question_id = q.question_id) as tag_count,
                -- 是否有接受答案
                EXISTS (SELECT 1 FROM answer a WHERE a.question_id = q.question_id AND a.is_accepted = true) as has_accepted_answer,
                -- 最高答案得分
                COALESCE((SELECT MAX(score) FROM answer a WHERE a.question_id = q.question_id), 0) as max_answer_score,
                -- 是否可解答（仅当“5 天内有被接受且该回答分数 > 1”）
                CASE 
                    WHEN EXISTS (
                        SELECT 1
                        FROM answer a
                        WHERE a.question_id = q.question_id
                        AND a.is_accepted = true
                        AND a.score > 1
                        AND a.creation_date >= q.creation_date
                        AND a.creation_date <= q.creation_date + INTERVAL '5 days'
                    ) THEN true
                    ELSE false
                END as is_solvable
            FROM question q
            LEFT JOIN question_title qt ON qt.question_id = q.question_id
            WHERE q.creation_date >= :start
            AND q.creation_date < :end
            AND qt.body IS NOT NULL
            """;

        return jdbcTemplate.queryForList(
                sql,
                Map.of("start", start, "end", end)
        );
    }

    /**
     * 按是否可解答分组统计各种因素
     */
    public List<Map<String, Object>> getGroupedMetrics(LocalDate start, LocalDate end) {

        String sql = """
            WITH metrics AS (
                SELECT 
                    CASE 
                        WHEN EXISTS (
                            SELECT 1
                            FROM answer a
                            WHERE a.question_id = q.question_id
                            AND a.is_accepted = true
                            AND a.score > 1
                            AND a.creation_date >= q.creation_date
                            AND a.creation_date <= q.creation_date + INTERVAL '5 days'
                        ) THEN true
                        ELSE false
                    END as is_solvable,
                    CASE 
                        WHEN qt.body LIKE '%<code>%' OR qt.body LIKE '%```%' 
                             OR qt.body ~* 'public\\s+class|void\\s+main' 
                        THEN true 
                        ELSE false 
                    END as has_code_snippet,
                    LENGTH(qt.body) as description_length,
                    (SELECT COUNT(*) FROM question_tag qtg WHERE qtg.question_id = q.question_id) as tag_count,
                    q.answer_count
                FROM question q
                LEFT JOIN question_title qt ON qt.question_id = q.question_id
                WHERE q.creation_date >= :start
                AND q.creation_date < :end
                AND qt.body IS NOT NULL
            )
            SELECT 
                is_solvable,
                COUNT(*) as question_count,
                AVG(CASE WHEN has_code_snippet THEN 1.0 ELSE 0.0 END) * 100 as code_snippet_percentage,
                AVG(description_length) as avg_description_length,
                AVG(tag_count::float) as avg_tag_count,
                AVG(answer_count::float) as avg_answer_count
            FROM metrics
            GROUP BY is_solvable
            """;

        return jdbcTemplate.queryForList(
                sql,
                Map.of("start", start, "end", end)
        );
    }
}
