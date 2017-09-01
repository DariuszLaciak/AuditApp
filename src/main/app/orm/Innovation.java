package main.app.orm;

import main.app.enums.InnovationCategory;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table
@Entity
public class Innovation implements ObjectDTO {
    private long id;
    private Date date;
    private User loggedUser;
    private InnovationCategory category;
    private List<InnovationAnswer> answers;

    public Innovation() {
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
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @OneToMany(mappedBy = "innovation", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    @PrimaryKeyJoinColumn
    public List<InnovationAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<InnovationAnswer> answers) {
        this.answers = answers;
    }

    @ManyToOne
    @JoinColumn(name = "loggedUser", nullable = false)
    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Column(nullable = false)
    public InnovationCategory getCategory() {
        return category;
    }

    public void setCategory(InnovationCategory category) {
        this.category = category;
    }
}
