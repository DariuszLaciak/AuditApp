package main.app.orm;

import main.app.enums.AuditType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Table
@Entity
public class Audit implements ObjectDTO {
    private long id;

    private Date auditDate;
    private AuditType type = AuditType.GENERAL;

    private User auditor;

    private List<Answer> answers;
    private AuditResult result;

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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "audit", cascade = CascadeType.ALL)
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

    @Column(nullable = false)
    public AuditType getType() {
        return type;
    }

    public void setType(AuditType type) {
        this.type = type;
    }
}
