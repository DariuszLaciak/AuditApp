package main.app.orm;

import javax.persistence.*;

/**
 * Created by darek on 22.05.17.
 */
@Table
@Entity
public class Opinion implements ObjectDTO {
    private long id;
    private String content;

    private User author;
    private Idea idea;

    public Opinion() {
    }

    public Opinion(String content) {
        this.content = content;
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
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author", nullable = false)
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ideaId")
    public Idea getIdea() {
        return idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
    }
}
