package main.app.orm;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Table
@Entity
public class HelpDictionary implements ObjectDTO {
    private long id;
    private String content;
    private String word;

    public HelpDictionary() {
    }

    public HelpDictionary(String content, String word) {
        this.content = content;
        this.word = word;
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

    @Column(nullable = false)
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
