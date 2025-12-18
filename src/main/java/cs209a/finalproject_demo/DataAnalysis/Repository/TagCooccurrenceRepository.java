package cs209a.finalproject_demo.DataAnalysis.Repository;

import cs209a.finalproject_demo.DataAnalysis.dto.TagPairCount;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class TagCooccurrenceRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TagCooccurrenceRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TagPairCount> topTagPairs(
            LocalDate start, LocalDate end, int topN) {

        String sql = """
            select
              least(t1.tag, t2.tag) as tag_a,
              greatest(t1.tag, t2.tag) as tag_b,
              count(*) as co_count
            from question_tag t1
            join question_tag t2
              on t1.question_id = t2.question_id
             and t1.tag < t2.tag
            join question q
              on q.question_id = t1.question_id
            where q.creation_date >= :start
              and q.creation_date < :end
              and t1.tag <> 'java'
              and t2.tag <> 'java'
            group by tag_a, tag_b
            order by co_count desc
            limit :topN
        """;

        return jdbcTemplate.query(
                sql,
                Map.of("start", start, "end", end, "topN", topN),
                (rs, i) -> new TagPairCount(
                        rs.getString("tag_a"),
                        rs.getString("tag_b"),
                        rs.getLong("co_count")
                )
        );
    }
}


