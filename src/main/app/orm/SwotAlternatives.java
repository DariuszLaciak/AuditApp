package main.app.orm;

import main.app.enums.SwotCategory;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
public class SwotAlternatives {
    private long id;
    private SwotCategory category;
    private String text;

    private List<Audit> audits;

    public SwotAlternatives() {
    }

    public SwotAlternatives(SwotCategory category, String text) {
        this.category = category;
        this.text = text;
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
    public SwotCategory getCategory() {
        return category;
    }

    public void setCategory(SwotCategory category) {
        this.category = category;
    }

    @Column(nullable = false)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @ManyToMany(mappedBy = "swot")
    public List<Audit> getAudits() {
        return audits;
    }

    public void setAudits(List<Audit> audits) {
        this.audits = audits;
    }
}
