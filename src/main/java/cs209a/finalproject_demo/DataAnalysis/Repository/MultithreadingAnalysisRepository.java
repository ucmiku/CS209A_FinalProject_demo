package cs209a.finalproject_demo.DataAnalysis.Repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class MultithreadingAnalysisRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MultithreadingAnalysisRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询多线程相关问题（基于标签和正文关键词）
     */
    public List<Map<String, Object>> findMultithreadingQuestions(
            LocalDate start, LocalDate end) {

        String sql = """
            WITH multithreading_questions AS (
                SELECT DISTINCT qt.question_id
                FROM question_title qt
                WHERE (
                    -- 基于正文内容的关键词匹配
                    LOWER(qt.body) LIKE ANY(ARRAY[
                        '%race condition%',
                        '%deadlock%',
                        '%livelock%',
                        '%starvation%',
                        '%synchronized%',
                        '%volatile%',
                        '%atomic%',
                        '%threadsafe%',
                        '%concurrent%',
                        '%interruptedexception%',
                        '%executorservice%',
                        '%forkjoinpool%',
                        '%threadpoolexecutor%',
                        '%completablefuture%'
                    ])
                    OR EXISTS (
                        -- 基于多线程相关标签
                        SELECT 1 FROM question_tag qtg
                        WHERE qtg.question_id = qt.question_id
                        AND qtg.tag IN (
                            'multithreading', 'concurrency', 'thread-safety',
                            'synchronization', 'deadlock', 'race-condition'
                        )
                    )
                )
                AND EXISTS (
                    -- 确保在指定时间范围内
                    SELECT 1 FROM question q
                    WHERE q.question_id = qt.question_id
                    AND q.creation_date >= :start
                    AND q.creation_date < :end
                )
            )
            SELECT 
                qt.question_id,
                qt.title,
                qt.body,
                q.creation_date,
                q.answer_count,
                q.score,
                STRING_AGG(qtg.tag, ',') AS tags
            FROM question_title qt
            JOIN question q ON q.question_id = qt.question_id
            LEFT JOIN question_tag qtg ON qtg.question_id = qt.question_id
            WHERE qt.question_id IN (SELECT question_id FROM multithreading_questions)
            GROUP BY qt.question_id, qt.title, qt.body, q.creation_date, q.answer_count, q.score
            """;

        return jdbcTemplate.queryForList(
                sql,
                Map.of("start", start, "end", end)
        );
    }

    /**
     * 统计各类多线程问题的数量
     */
    public List<Map<String, Object>> countIssueCategories(
            LocalDate start, LocalDate end) {

        String sql = """
            WITH categorized AS (
                SELECT 
                    qt.question_id,
                    CASE 
                        WHEN LOWER(qt.body) LIKE '%race condition%' THEN 'RACE_CONDITION'
                        WHEN LOWER(qt.body) LIKE '%deadlock%' THEN 'DEADLOCK'
                        WHEN LOWER(qt.body) LIKE '%volatile%' OR LOWER(qt.body) LIKE '%memory visibility%' 
                            THEN 'MEMORY_VISIBILITY'
                        WHEN LOWER(qt.body) LIKE '%threadpool%' OR LOWER(qt.body) LIKE '%executor%' 
                            THEN 'THREADPOOL_MISUSE'
                        WHEN LOWER(qt.body) LIKE '%concurrentmodification%' THEN 'CONCURRENT_MODIFICATION'
                        WHEN LOWER(qt.body) LIKE '%interrupted%' THEN 'THREAD_INTERRUPTION'
                        ELSE 'GENERAL_MULTITHREADING'
                    END as issue_category
                FROM question_title qt
                JOIN question q ON q.question_id = qt.question_id
                WHERE (
                    LOWER(qt.body) LIKE ANY(ARRAY[
                        '%race condition%',
                        '%deadlock%',
                        '%volatile%',
                        '%memory visibility%',
                        '%threadpool%',
                        '%executor%',
                        '%concurrentmodification%',
                        '%interrupted%'
                    ])
                    OR EXISTS (
                        SELECT 1 FROM question_tag qtg
                        WHERE qtg.question_id = qt.question_id
                        AND qtg.tag IN ('multithreading', 'concurrency')
                    )
                )
                AND q.creation_date >= :start
                AND q.creation_date < :end
            )
            SELECT 
                issue_category,
                COUNT(*) as count,
                STRING_AGG(question_id::text, ',') as example_ids
            FROM categorized
            GROUP BY issue_category
            ORDER BY count DESC
            """;

        return jdbcTemplate.queryForList(
                sql,
                Map.of("start", start, "end", end)
        );
    }
}
