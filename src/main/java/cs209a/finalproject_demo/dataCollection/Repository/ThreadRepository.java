package cs209a.finalproject_demo.dataCollection.Repository;

import cs209a.finalproject_demo.dataCollection.QuestionThread;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadRepository extends JpaRepository<QuestionThread,Long> {

}
