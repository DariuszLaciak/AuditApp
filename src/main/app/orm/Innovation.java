package main.app.orm;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table
@Entity
public class Innovation implements ObjectDTO {
    private long id;
    private Date date;
    private String innovationName;
    private String companyName;
    private String attachments;
    private String signed;

    private User loggedUser;
    private List<InnovationAnswer> answers;

    public Innovation() {
    }

    public Innovation(String innovationName, String companyName) {
        this.date = new Date();
        this.innovationName = innovationName;
        this.companyName = companyName;
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

    @Column(nullable = false)
    @Type(type = "text")
    public String getInnovationName() {
        return innovationName;
    }

    public void setInnovationName(String innovationName) {
        this.innovationName = innovationName;
    }

    @Column(nullable = false)
    @Type(type = "text")
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    @Column
    @Type(type = "text")
    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    @Column(nullable = false)
    @Type(type = "text")
    public String getSigned() {
        return signed;
    }

    public void setSigned(String signed) {
        this.signed = signed;
    }

    @ManyToOne
    @JoinColumn(name = "loggedUser", nullable = false)
    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }
}
