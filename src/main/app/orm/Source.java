package main.app.orm;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
public class Source implements ObjectDTO {
    private long id;
    private String text;
    private String longDescription;

    private List<Audit> audits;

    public Source() {
    }

    public Source(String text, String longDescription) {
        this.text = text;
        this.longDescription = longDescription;
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
    @Type(type = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Column(nullable = false)
    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @ManyToMany(mappedBy = "sources", cascade = CascadeType.ALL)
    public List<Audit> getAudits() {
        return audits;
    }

    public void setAudits(List<Audit> audits) {
        this.audits = audits;
    }

    @Override
    public boolean equals(Object o) {
        Source a = (Source) o;
        return this.getId() == a.getId();
    }
}
