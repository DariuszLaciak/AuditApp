package main.app.orm;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
public class Impediment {
    private long id;
    private String text;

    private List<ImpedimentAdvice> advices;

    private List<Audit> audits;

    public Impediment() {
    }

    public Impediment(String text) {
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

    @Column(nullable = false, length = 70)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "impediment", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    @PrimaryKeyJoinColumn
    public List<ImpedimentAdvice> getAdvices() {
        return advices;
    }

    public void setAdvices(List<ImpedimentAdvice> advices) {
        this.advices = advices;
    }

    @ManyToMany(mappedBy = "impediments", cascade = CascadeType.ALL)
    public List<Audit> getAudits() {
        return audits;
    }

    public void setAudits(List<Audit> audits) {
        this.audits = audits;
    }
}
