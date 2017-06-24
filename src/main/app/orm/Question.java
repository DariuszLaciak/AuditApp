package main.app.orm;

import main.app.enums.QuestionCategory;
import main.app.enums.QuestionType;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Darek on 2017-04-09.
 */

@Table
@Entity
public class Question implements ObjectDTO {
    private long id;
    private String content;
    private QuestionType type;
    private QuestionCategory category;

    private List<Answer> answers;


    public Question() {
    }

    public Question(String content, QuestionType type, QuestionCategory category) {
        this.content = content;
        this.type = type;
        this.category = category;
    }

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
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(nullable = false)
    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    @OneToMany(mappedBy = "question", orphanRemoval = true, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Column(nullable = false)
    public QuestionCategory getCategory() {
        return category;
    }

    public void setCategory(QuestionCategory category) {
        this.category = category;
    }
}
