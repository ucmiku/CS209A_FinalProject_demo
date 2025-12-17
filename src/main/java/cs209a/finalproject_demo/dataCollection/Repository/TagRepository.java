package cs209a.finalproject_demo.dataCollection.Repository;

import cs209a.finalproject_demo.dataCollection.Attributes.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, String> {}
