package com.example.begzug;

import com.example.begzug.Article;
import javax.persistence.*;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "Author")
public class Author {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @Column(name = "author_id")
    private Integer id;
    private String name;
    private String surname;
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}