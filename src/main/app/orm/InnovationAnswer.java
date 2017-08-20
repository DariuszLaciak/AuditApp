package main.app.orm;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Table
@Entity
public class InnovationAnswer implements ObjectDTO {
    private long id;
    private String content;
    private String additionalAnswer;

    private InnovationQuestion question;
    private Innovation innovation;

    public InnovationAnswer() {
    }

    public InnovationAnswer(String content) {
        this.content = content;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(nullable = false)
    @Type(type = "text")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ManyToOne
    @JoinColumn(name = "question", nullable = false)
    public InnovationQuestion getQuestion() {
        return question;
    }

    public void setQuestion(InnovationQuestion question) {
        this.question = question;
    }

    @ManyToOne
    @JoinColumn(name = "innovation", nullable = false)
    public Innovation getInnovation() {
        return innovation;
    }

    public void setInnovation(Innovation innovation) {
        this.innovation = innovation;
    }

    @Column
    @Type(type = "text")
    public String getAdditionalAnswer() {
        return additionalAnswer;
    }

    public void setAdditionalAnswer(String additionalAnswer) {
        this.additionalAnswer = additionalAnswer;
    }
}
