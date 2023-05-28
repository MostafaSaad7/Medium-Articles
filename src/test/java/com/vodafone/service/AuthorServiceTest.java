package com.vodafone.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.vodafone.model.Author;
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
class AuthorServiceTest {

    @Autowired
    private AuthorService authorService;

    @MockBean
    private AuthorRepository authorRepository;

    @TestConfiguration
    @Primary
    static class ArticleServiceContextConfiguration {
        public AuthorService articleService() {
            return new AuthorServiceImpl();
        }
    }

    @Test
    void getAuthorByIdTest_acceptArticleId_returnArticleWithGivenId() {

        int authorId = 1;
        String authorName = "Test";
        Author author = new Author();
        author.setId(authorId);
        author.setName(authorName);

        // Mock the articleRepository.findById method to return the test article
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        Author result = authorService.getAuthorById(authorId);

        // Verify the result
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(authorId);
        assertThat(result.getName()).isEqualTo(authorName);
        verify(authorRepository, times(1)).findById(authorId);
    }

    @Test
    void addAuthorTest_acceptArticleObject_returnCreatedArticle() {
        int authorId = 1;
        String authorName = "test";
        Author author = new Author();
        author.setId(authorId);
        author.setName(authorName);

        when(authorRepository.save(author)).thenReturn(author);

        Author author1 = authorService.addAuthor(author);

        assertNotNull(author1);
        assertEquals(authorName, author1.getName());
    }

    @Test
    void getAllAuthorsTest_acceptPageAndPageSize_returnArticlesInThisPage() {

        int page = 0;
        int pageSize = 2;

        Author author1 = new Author();
        Author author2 = new Author();
        List<Author> authorList = Arrays.asList(author1, author2);

        // Create a mock page of articles
        Page<Author> authorPage = mock(Page.class);
        when(authorPage.getContent()).thenReturn(authorList);
        when(authorPage.getTotalPages()).thenReturn(1);
        // Mock the articleRepository.findAll() method to return the mock page
        when(authorRepository.findAll(PageRequest.of(page, pageSize))).thenReturn(authorPage);

        // Call the getAllArticles method
        List<Author> result = authorService.getAllAuthors(page, pageSize);

        // Verify the results
        assertThat(result).hasSize(pageSize).contains(author1, author2);
        verify(authorRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void deleteAuthorByNameTest_acceptArticleId_verifyThatArticleHasBeenDeleted() {
        // Mock input parameters
        String authorName="test";
        when(authorRepository.findAllByName(authorName)).thenReturn(
                Arrays.asList(new Author(),new Author())
        );
        // Mock the repository delete operation
        when(authorRepository.deleteAllByName(authorName)).thenReturn(Arrays.asList(new Author(),new Author()));

        // Call the service method
        authorService.deleteAuthorByName(authorName);

        // Verify the repository delete operation was called
        verify(authorRepository, times(1)).deleteAllByName(authorName);
    }
    @Test
    void deleteAuthorByIdTest_acceptArticleId_verifyThatArticleHasBeenDeleted() {
        // Mock input parameters
        Integer authorId = 1;

        when(authorRepository.findById(anyInt())).thenReturn(
                Optional.of(new Author())
        );
        // Mock the repository delete operation
        doNothing().when(authorRepository).deleteById(authorId);

        // Call the service method
        authorService.deleteAuthorById(authorId);

        // Verify the repository delete operation was called
        verify(authorRepository, times(1)).deleteById(authorId);
    }
    @Test
    void updateAuthorTest_acceptArticleIdAndUpdatedArticle_returnUpdatedArticle() {

        int authorId = 1;
        String authorName = "test";
        String updatedAuthorName = "test2";
        Author author = new Author();
        author.setId(authorId);
        author.setName(authorName);

        Author updatedAuthor = new Author();
        updatedAuthor.setId(authorId);
        updatedAuthor.setName(updatedAuthorName);
        // Mock the article repository
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(updatedAuthor);

        // Call the updateArticle method
        Author result = authorService.updateAuthor(authorId, updatedAuthor);

        // Verify the repository method calls
        verify(authorRepository, times(1)).findById(authorId);
        verify(authorRepository, times(1)).save(author);

        // Verify the updated article properties
        // Note in method logic existing article updated then saved again to DB so just check if
        // existing article's field updated
        assertEquals(author.getName(), result.getName());
    }

}