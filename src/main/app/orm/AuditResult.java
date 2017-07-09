package main.app.orm;

import main.app.enums.SwotResult;

import javax.persistence.*;


@Table
@Entity
public class AuditResult implements ObjectDTO {
    private long id;

    private int resultValue;
    private SwotResult swotResult;

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

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "auditId")
    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    @Column
    public SwotResult getSwotResult() {
        return swotResult;
    }

    public void setSwotResult(SwotResult swotResult) {
        this.swotResult = swotResult;
    }

    @Override
    public boolean equals(Object o) {
        AuditResult a = (AuditResult) o;
        return this.getId() == a.getId();
    }
}
