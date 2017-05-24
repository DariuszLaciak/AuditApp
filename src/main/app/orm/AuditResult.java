package main.app.orm;

import javax.persistence.*;


@Table
@Entity
public class AuditResult implements ObjectDTO {
    private long id;

    private int resultValue;

    private Audit audit;

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
    public int getResultValue() {
        return resultValue;
    }

    public void setResultValue(int resultValue) {
        this.resultValue = resultValue;
    }

    @OneToOne(optional = false)
    @JoinColumn(name = "auditId")
    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }
}
