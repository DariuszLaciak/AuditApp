package main.app.orm;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by darek on 22.05.17.
 */
@Table
@Entity
public class Opinion implements ObjectDTO {
    private long id;
    private String content;
    private Date opinionDate;

    private User author;
    private Idea idea;

    public Opinion() {
    }

    public Opinion(String content) {
        this.content = content;
        this.opinionDate = new Date();
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
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ManyToOne
    @JoinColumn(name = "author", nullable = false)
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @ManyToOne
    @JoinColumn(name = "ideaId", nullable = false)
    public Idea getIdea() {
        return idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
    }

    @Column
    public Date getOpinionDate() {
        return opinionDate;
    }

    public void setOpinionDate(Date opinionDate) {
        this.opinionDate = opinionDate;
    }

    @Override
    public boolean equals(Object o) {
        Opinion a = (Opinion) o;
        return this.getId() == a.getId();
    }
}
