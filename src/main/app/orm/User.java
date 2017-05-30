package main.app.orm;

import main.app.enums.LoginType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table
@Entity
public class User implements ObjectDTO {


    private long id;
    private LoginType role;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private boolean active = false;
    private Date accountCreated;

    private List<Audit> audits;
    private List<Idea> ideas;
    private List<Opinion> opinions;

    public User() {
    }

    public User(String username, String password) {
        this.role = LoginType.USER;
        this.username = username;
        this.password = password;
    }

    public User(LoginType role, String username, String password, String name, String surname, String email, boolean active) {
        this.role = role;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.active = active;
        this.accountCreated = new Date();
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
    public LoginType getRole() {
        return role;
    }

    public void setRole(LoginType role) {
        this.role = role;
    }

    @Column(nullable = false, length = 20)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(nullable = false, length = 70)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(length = 70)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 70)
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Column(length = 70)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(nullable = false, length = 70)
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @OneToMany(mappedBy = "auditor")
    @Fetch(value = FetchMode.SUBSELECT)
    @PrimaryKeyJoinColumn
    public List<Audit> getAudits() {
        return audits;
    }

    public void setAudits(List<Audit> audits) {
        this.audits = audits;
    }

    @OneToMany(mappedBy = "employee")
    @Fetch(value = FetchMode.SUBSELECT)
    @PrimaryKeyJoinColumn
    public List<Idea> getIdeas() {
        return ideas;
    }

    public void setIdeas(List<Idea> ideas) {
        this.ideas = ideas;
    }

    @OneToMany(mappedBy = "author")
    @Fetch(value = FetchMode.SUBSELECT)
    @PrimaryKeyJoinColumn
    public List<Opinion> getOpinions() {
        return opinions;
    }

    public void setOpinions(List<Opinion> opinions) {
        this.opinions = opinions;
    }

    @Column
    public Date getAccountCreated() {
        return accountCreated;
    }

    public void setAccountCreated(Date accountCreated) {
        this.accountCreated = accountCreated;
    }
}
