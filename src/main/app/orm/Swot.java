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
public class Swot implements ObjectDTO {
    private long id;
    private Date swotDate;

    private User auditorId;

    @LazyCollection(LazyCollectionOption.FALSE)
    private List<SwotRelations> relations;

    @LazyCollection(LazyCollectionOption.FALSE)
    private List<SwotAlternatives> alternatives;

    public Swot() {
    }

    public Swot(Date swotDate) {
        this.swotDate = swotDate;
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
    public Date getSwotDate() {
        return swotDate;
    }

    public void setSwotDate(Date swotDate) {
        this.swotDate = swotDate;
    }

    @ManyToOne
    @JoinColumn(name = "auditorId", nullable = false)
    public User getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(User auditorId) {
        this.auditorId = auditorId;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "swot", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    @PrimaryKeyJoinColumn
    public List<SwotRelations> getRelations() {
        return relations;
    }

    public void setRelations(List<SwotRelations> relations) {
        this.relations = relations;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "swotAlts", joinColumns = {
            @JoinColumn(name = "swotId", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "swotAlternativeId",
                    nullable = false, updatable = false)})
    public List<SwotAlternatives> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<SwotAlternatives> alternatives) {
        this.alternatives = alternatives;
    }

    @Override
    public boolean equals(Object o) {
        Swot a = (Swot) o;
        return this.getId() == a.getId();
    }
}
