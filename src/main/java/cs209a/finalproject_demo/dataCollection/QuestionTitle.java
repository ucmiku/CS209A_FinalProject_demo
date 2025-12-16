package cs209a.finalproject_demo.dataCollection;

import jakarta.persistence.*;

@Entity
@Table(name = "question_title")
public class QuestionTitle {

    @Id
    @Column(name = "question_id")
    private Long questionId;

    @Column(nullable = false, length = 512)
    private String title;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}


