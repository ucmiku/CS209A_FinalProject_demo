package cs209a.finalproject_demo.dataCollection.Attributes;

import jakarta.persistence.*;

@Entity
@Table(
        name = "question_tag",
        indexes = {
                @Index(name = "idx_qtag_tag", columnList = "tag"),
                @Index(name = "idx_qtag_qid", columnList = "question_id")
        }
)
@IdClass(QuestionTagId.class)
public class QuestionTag {

    @Id
    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Id
    @Column(name = "tag", nullable = false, length = 100)
    private String tag;

    public QuestionTag() {}

    public QuestionTag(Long questionId, String tag) {
        this.questionId = questionId;
        this.tag = tag;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
