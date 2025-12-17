package cs209a.finalproject_demo.dataCollection.Repository;

import cs209a.finalproject_demo.dataCollection.Attributes.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {}

