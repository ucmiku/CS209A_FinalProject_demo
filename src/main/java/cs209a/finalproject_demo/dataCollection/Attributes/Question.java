package cs209a.finalproject_demo.dataCollection.Attributes;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "question")
public class Question {

    @Id
    private Long questionId;

    private boolean isAnswered;
    private int answerCount;
    private int viewCount;
    private int score;

    private String tags;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String body;
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getScore() {
        return score;
    }

    public String getTags() {
        return tags;
    }
}
