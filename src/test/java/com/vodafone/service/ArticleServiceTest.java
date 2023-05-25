package com.vodafone.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.vodafone.model.Article;
import com.vodafone.model.Author;
import com.vodafone.repository.ArticleRepository;
import com.vodafone.repository.AuthorRepository;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ArticleServiceTest {
    @Autowired
    private ArticleService articleService;
    @MockBean
    private ArticleRepository articleRepository;
    @MockBean
    private AuthorRepository authorRepository;
    @TestConfiguration
    @Primary
    static class ArticleServiceContextConfiguration
    {
        public ArticleService articleService(){
            return new ArticleServiceImpl();
        }
    }
    @Test
    void getAllArticlesTest_acceptPageAndPageSize_returnArticlesInThisPage() {

        int page=0;
        int pageSize=2;

        Article article1 = new Article();
        Article article2 = new Article();
        List<Article> articleList = Arrays.asList(article1, article2);

        // Create a mock page of articles
        Page<Article> articlePage = mock(Page.class);
        when(articlePage.getContent()).thenReturn(articleList);
        when(articlePage.getTotalPages()).thenReturn(1);
        // Mock the articleRepository.findAll() method to return the mock page
        when(articleRepository.findAll(PageRequest.of(page, pageSize))).thenReturn(articlePage);

        // Call the getAllArticles method
        List<Article> result = articleService.getAllArticles(page, pageSize);

        // Verify the results
        assertThat(result).hasSize(pageSize).contains(article1, article2);
        verify(articleRepository, times(1)).findAll(any(PageRequest.class));
    }
    @Test
    void getArticleByIdTest_acceptArticleId_returnArticleWithGivenId() {

        int articleId = 1;
        String articleName="Test Article";
        Article article = new Article();
        article.setId(articleId);
        article.setName(articleName);

        // Mock the articleRepository.findById method to return the test article
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        Article result = articleService.getArticleById(articleId);

        // Verify the result
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(articleId);
        assertThat(result.getName()).isEqualTo(articleName);
        verify(articleRepository, times(1)).findById(articleId);
    }
    @Test
    void getArticlesByAuthorName() {
        String authorName = "Mostafa Saad";
        Article article1 = new Article();
        article1.setAuthor(authorName);
        Article article2 = new Article();
        article2.setAuthor(authorName);
        List<Article> articles = Arrays.asList(article1, article2);

        when(articleRepository.findByAuthorContains(authorName)).thenReturn(articles);

        List<Article> result = articleService.getArticlesByAuthorName(authorName);

        assertThat(result).hasSize(2)
                .contains(article1, article2);
    }

    @Test
    void addArticle() {
        Article article = new Article();
        article.setAuthorId(1);
        article.setName("test");
        article.setAuthor("saad");

        Author author = new Author();
        author.setName("saad");

        when(articleRepository.existsById(anyInt())).thenReturn(false);
        when(authorRepository.existsById(anyInt())).thenReturn(true);
        when(authorRepository.findById(anyInt())).thenReturn(Optional.of(author));
        when(articleRepository.save(article)).thenReturn(article);

        Article article1 = articleService.addArticle(article);

        assertNotNull(article1);
        // Add additional assertions if needed
        assertEquals("test", article1.getName());
        assertEquals("saad", article1.getAuthor());


    }

    @Test
    void deleteArticle() {
        // Mock input parameters
        Integer articleId = 1;

        when(articleRepository.findById(anyInt())).thenReturn(
                Optional.of(new Article())
        );
        // Mock the repository delete operation
        doNothing().when(articleRepository).deleteById(articleId);

        // Call the service method
        articleService.deleteArticle(articleId);

        // Verify the repository delete operation was called
        verify(articleRepository, times(1)).deleteById(articleId);
    }
    @Test
    void updateArticle() {
        // Create a sample article
        Article existingArticle = new Article();
        existingArticle.setId(1); // Set the existing article's ID
        existingArticle.setAuthor("John Doe");
        existingArticle.setName("Sample Article");
        existingArticle.setAuthorId(123);

        // Create a modified article with updated data
        Article updatedArticle = new Article();
        updatedArticle.setAuthor("Jane Smith");
        updatedArticle.setName("Updated Article");
        updatedArticle.setAuthorId(456);

        // Mock the article repository
        when(articleRepository.findById(1)).thenReturn(Optional.of(existingArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(updatedArticle);

        // Call the updateArticle method
        Article result = articleService.updateArticle(1, updatedArticle);

        // Verify the repository method calls
        verify(articleRepository, times(1)).findById(1);
        verify(articleRepository, times(1)).save(existingArticle);

        // Verify the updated article properties
        // Note in method logic existing article updated then saved agian to DB so just check if
        // existing article's field updated
        assertEquals(existingArticle.getAuthor(), result.getAuthor());
        assertEquals(existingArticle.getName(), result.getName());
        assertEquals(existingArticle.getAuthorId(), result.getAuthorId());
    }

}