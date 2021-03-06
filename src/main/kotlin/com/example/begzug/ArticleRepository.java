package com.example.begzug;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ArticleRepository extends CrudRepository<Article, Integer> {
    Article findById(int id);
    List<Article> findArticlesByAuthor(Author author);
    List<Article> findArticlesBySight(Sight sight);
}