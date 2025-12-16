package cs209a.finalproject_demo.dataCollection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionTitleRepository
        extends JpaRepository<QuestionTitle, Long> {
    @Query("select qt.questionId from QuestionTitle qt")
    List<Long> findAllQuestionIds();
}

