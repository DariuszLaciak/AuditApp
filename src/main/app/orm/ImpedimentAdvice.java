package main.app.orm;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Table
@Entity
public class ImpedimentAdvice implements ObjectDTO {
    private long id;
    private String text;

    private Impediment impediment;

    public ImpedimentAdvice() {
    }

    public ImpedimentAdvice(String text) {
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
    @Type(type = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @ManyToOne
    @JoinColumn(name = "impedimentId")
    public Impediment getImpediment() {
        return impediment;
    }

    public void setImpediment(Impediment impediment) {
        this.impediment = impediment;
    }

    @Override
    public boolean equals(Object o) {
        ImpedimentAdvice a = (ImpedimentAdvice) o;
        return this.getId() == a.getId();
    }
}
