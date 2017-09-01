package main.app.orm;

import main.app.enums.InnovationCategory;
import main.app.enums.InnovationType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
public class InnovationQuestion implements ObjectDTO {
    private long id;
    private String label;
    private boolean additional = false;
    private InnovationType type;
    private InnovationCategory category;
    private String placeholder;
    private boolean isLonger = false;

    private InnovationQuestion relatedQuestion;
    private List<InnovationQuestion> relatedQuestions;
    private List<Answer> answers;

    public InnovationQuestion() {
    }

    public InnovationQuestion(String label, boolean additional, InnovationType type, InnovationCategory category, boolean isLonger) {
        this.label = label;
        this.additional = additional;
        this.type = type;
        this.category = category;
        this.isLonger = isLonger;
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
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(nullable = false)
    public boolean isAdditional() {
        return additional;
    }

    public void setAdditional(boolean additional) {
        this.additional = additional;
    }

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public InnovationType getType() {
        return type;
    }

    public void setType(InnovationType type) {
        this.type = type;
    }

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    @PrimaryKeyJoinColumn
    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public InnovationCategory getCategory() {
        return category;
    }

    public void setCategory(InnovationCategory category) {
        this.category = category;
    }

    @Column
    @Type(type = "text")
    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Column(nullable = false)
    public boolean isLonger() {
        return isLonger;
    }

    public void setLonger(boolean aLong) {
        isLonger = aLong;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    public InnovationQuestion getRelatedQuestion() {
        return relatedQuestion;
    }

    public void setRelatedQuestion(InnovationQuestion relatedQuestion) {
        this.relatedQuestion = relatedQuestion;
    }

    @OneToMany(mappedBy = "relatedQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<InnovationQuestion> getRelatedQuestions() {
        return relatedQuestions;
    }

    public void setRelatedQuestions(List<InnovationQuestion> relatedQuestions) {
        this.relatedQuestions = relatedQuestions;
    }
}
