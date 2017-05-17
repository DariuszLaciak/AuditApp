package main.app.orm;

import javax.persistence.*;

@Table
@Entity
public class Answer implements ObjectDTO {
    private long id;
    private float answer;
    private long yesValueAnswer;

    private Audit audit;
    private Question question;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @Column(nullable = false)
    public float getAnswer() {
        return answer;
    }

    public void setAnswer(float answer) {
        this.answer = answer;
    }

    @Column(length = 10)
    public long getYesValueAnswer() {
        return yesValueAnswer;
    }

    public void setYesValueAnswer(long yesValueAnswer) {
        this.yesValueAnswer = yesValueAnswer;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auditId", nullable = false)
    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit auditId) {
        this.audit = auditId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionId", nullable = false)
    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question questionId) {
        this.question = questionId;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", answer=" + answer +
                ", yesValueAnswer=" + yesValueAnswer +
                ", audit=" + audit +
                ", question=" + question +
                '}';
    }
}
