package main.app.orm;

import javax.persistence.*;

@Table
@Entity
public class SwotRelations {
    private long id;
    private int relation;

    private Audit audit;
    private SwotAlternatives relationPartner1;
    private SwotAlternatives relationPartner2;

    public SwotRelations() {
    }

    public SwotRelations(int relation, Audit audit, SwotAlternatives relationPartner1, SwotAlternatives relationPartner2) {
        this.relation = relation;
        this.audit = audit;
        this.relationPartner1 = relationPartner1;
        this.relationPartner2 = relationPartner2;
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
    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    @ManyToOne
    @JoinColumn(name = "auditId")
    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    @ManyToOne
    @JoinColumn(name = "partner1Id")
    public SwotAlternatives getRelationPartner1() {
        return relationPartner1;
    }

    public void setRelationPartner1(SwotAlternatives relationPartner1) {
        this.relationPartner1 = relationPartner1;
    }

    @ManyToOne
    @JoinColumn(name = "partner2Id")
    public SwotAlternatives getRelationPartner2() {
        return relationPartner2;
    }

    public void setRelationPartner2(SwotAlternatives relationPartner2) {
        this.relationPartner2 = relationPartner2;
    }
}
