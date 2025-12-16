package cs209a.finalproject_demo.dataCollection;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_thread")
public class QuestionThread {

    @Id
    @Column(name = "question_id")
    private Long questionId;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String question;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String answers;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String comments;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String tags;

    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;

    public LocalDateTime getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(LocalDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
