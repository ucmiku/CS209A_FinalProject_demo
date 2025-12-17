package cs209a.finalproject_demo.dataCollection.Repository;

import cs209a.finalproject_demo.dataCollection.Attributes.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository
        extends JpaRepository<Comment, Long> {}

