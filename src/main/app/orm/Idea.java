package main.app.orm;

import main.app.enums.IdeaTypes;
import main.app.enums.Status;

import javax.persistence.*;
import java.util.Date;

@Table
@Entity
public class Idea implements ObjectDTO {
    private long id;
    private String content;
    private Status status;
    private IdeaTypes type;
    private Date addedDate;
    private String name;
    private Date actionDate;

    private User employee;

    private Opinion opinion;

    public Idea() {
    }

    public Idea(String name, String content, IdeaTypes type) {
        this.name = name;
        this.content = content;
        this.status = Status.PENDING;
        this.type = type;
        this.addedDate = new Date();
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

    @Column(nullable = false)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee", nullable = false)
    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User emloyee) {
        this.employee = emloyee;
    }

    @Column(nullable = false)
    public IdeaTypes getType() {
        return type;
    }

    public void setType(IdeaTypes type) {
        this.type = type;
    }

    @Column(nullable = false)
    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    @Column(nullable = false, length = 70)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "idea")
    public Opinion getOpinion() {
        return opinion;
    }

    public void setOpinion(Opinion opinion) {
        this.opinion = opinion;
    }
}
