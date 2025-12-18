package cs209a.finalproject_demo.dataCollection.Repository;

import cs209a.finalproject_demo.dataCollection.Attributes.QuestionTag;
import cs209a.finalproject_demo.dataCollection.Attributes.QuestionTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionTagRepository
        extends JpaRepository<QuestionTag, QuestionTagId> {
}
