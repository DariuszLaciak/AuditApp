package main.app.orm;

import main.app.enums.SwotCategory;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
public class SwotAlternatives {
    private long id;
    private SwotCategory category;
    private String text;

    private List<Swot> swots;

    private List<SwotRelations> relations1;
    private List<SwotRelations> relations2;

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

    @ManyToMany(mappedBy = "alternatives", cascade = CascadeType.ALL)
    public List<Swot> getSwots() {
        return swots;
    }

    public void setSwots(List<Swot> swots) {
        this.swots = swots;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "relationPartner1", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    @PrimaryKeyJoinColumn
    public List<SwotRelations> getRelations1() {
        return relations1;
    }

    public void setRelations1(List<SwotRelations> relations1) {
        this.relations1 = relations1;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "relationPartner2", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    @PrimaryKeyJoinColumn
    public List<SwotRelations> getRelations2() {
        return relations2;
    }

    public void setRelations2(List<SwotRelations> relations2) {
        this.relations2 = relations2;
    }

    @Override
    public boolean equals(Object o) {
        SwotAlternatives a = (SwotAlternatives) o;
        return this.getId() == a.getId();
    }
}
