package cs209a.finalproject_demo.dataCollection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_entry")
public class QuestionThread {
    @Id
    private Long questionId;

    @Column(columnDefinition = "jsonb")
    private String question;      // 原始问题 JSON

    @Column(columnDefinition = "jsonb")
    private String answers;       // 原始回答列表 JSON

    @Column(columnDefinition = "jsonb")
    private String comments;      // 原始评论列表 JSON

    @Column(columnDefinition = "jsonb")
    private String tags;          // 原始 tags JSON

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public LocalDateTime getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(LocalDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    private LocalDateTime fetchedAt;
}
