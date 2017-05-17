package main.app.orm;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Table
@Entity
public class Audit implements ObjectDTO {
    private long id;
    private String name;
    private String regon;
    private String krs;
    private Date established;
    private int emloyees;
    private Date auditDate;

    private User auditor;

    private List<Answer> answers;
    private AuditResult result;

    public Audit() {
    }

    public Audit(String name, String regon, String krs, Date established, int emloyees) {
        this.name = name;
        this.regon = regon;
        this.krs = krs;
        this.established = established;
        this.emloyees = emloyees;
        this.auditDate = new Date();
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false, length = 20)
    public String getRegon() {
        return regon;
    }

    public void setRegon(String regon) {
        this.regon = regon;
    }

    @Column(nullable = false, length = 20)
    public String getKrs() {
        return krs;
    }

    public void setKrs(String krs) {
        this.krs = krs;
    }

    @Column(nullable = false)
    public Date getEstablished() {
        return established;
    }

    public void setEstablished(Date established) {
        this.established = established;
    }

    @Column(nullable = false, length = 5)
    public int getEmloyees() {
        return emloyees;
    }

    public void setEmloyees(int emloyees) {
        this.emloyees = emloyees;
    }

    @Column(nullable = false)
    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    @ManyToOne(fetch = FetchType.EAGER)
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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "audit")
    public AuditResult getResult() {
        return result;
    }

    public void setResult(AuditResult result) {
        this.result = result;
    }
}
