package cs209a.finalproject_demo.dataCollection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_tag")
@IdClass(QuestionTagId.class)
public class QuestionTag {

    @Id
    private Long questionId;

    @Id
    private String tagName;
}
