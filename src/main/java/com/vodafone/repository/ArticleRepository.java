package com.vodafone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vodafone.model.Article;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer>
{
    List<Article> findByAuthor(String author);
    List<Article> findByAuthorContains(String author);
}