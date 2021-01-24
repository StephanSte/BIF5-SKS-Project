package com.example.begzug;

import javax.persistence.*;

@Entity
@Table(name = "Article")
public class Article {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int id;
    private String title;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;


    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
