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

    @Column(columnDefinition = "text")
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

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


