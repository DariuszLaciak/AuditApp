package main.app.orm;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Table
@Entity
public class Audit implements ObjectDTO {
    private long id;

    private Date auditDate;

    private User auditor;

    private List<Answer> answers;
    private AuditResult result;

    @LazyCollection(LazyCollectionOption.FALSE)
    private List<SwotAlternatives> swot;

    public Audit() {
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
    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    @ManyToOne
    @JoinColumn(name = "auditorId", nullable = false)
    public User getAuditor() {
        return auditor;
    }

    public void setAuditor(User auditor) {
        this.auditor = auditor;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "audit")
    @PrimaryKeyJoinColumn
    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "audit")
    public AuditResult getResult() {
        return result;
    }

    public void setResult(AuditResult result) {
        this.result = result;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "auditSwot", joinColumns = {
            @JoinColumn(name = "auditId", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "swotAlternativeId",
                    nullable = false, updatable = false)})
    public List<SwotAlternatives> getSwot() {
        return swot;
    }

    public void setSwot(List<SwotAlternatives> swot) {
        this.swot = swot;
    }
}
