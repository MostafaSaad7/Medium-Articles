package com.vodafone.contoller;

import java.util.Arrays;
import java.util.List;

import com.vodafone.errorhandlling.ArticleNotFoundException;
import com.vodafone.model.Article;
import com.vodafone.service.ArticleService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;



import static org.mockito.BDDMockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ArticlesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    private String asJsonString(Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void getArticlesTest_sendGetRequest_shouldReturnAllArticles() throws Exception {
        // Arrange
        // Prepare test data
        Article article1 = new Article();
        Article article2 = new Article();
        List<Article> articleList = Arrays.asList(article1, article2);
        // Configure mock service
        when(articleService.getAllArticles(anyInt(), anyInt())).thenReturn(articleList);

        //Act + Assertion
        // Perform GET request
        mockMvc.perform(get("/v1/articles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // Verify mock service interaction (Assertion)
        verify(articleService, times(1)).getAllArticles(anyInt(), anyInt());
        verifyNoMoreInteractions(articleService);
    }


    @Test
    void getArticleByIDTest_SendGetRequest_shouldReturnArticleWithGivenID() throws Exception {
        int articleID = 1;
        String articleName= "test";
        Article article = new Article();
        article.setName(articleName);
        article.setId(articleID);
        when(articleService.getArticleById(articleID)).thenReturn(article);

        mockMvc.perform(get("/v1/articles/"+articleID).contentType(MediaType.APPLICATION_JSON_VALUE)).
                andExpect(status().isOk()).andExpect(jsonPath("$.name").value(articleName));

        verify(articleService, times(1)).getArticleById(anyInt());
        verifyNoMoreInteractions(articleService);
    }

    @Test
    void addArticleTest_sendRequestIncludingArticleInBody_expectStatusCodeCreated() throws Exception {
        int articleID = 1;
        String articleName= "test";
        Article article = new Article();
        article.setName(articleName);
        article.setId(articleID);
        when(articleService.addArticle(any(Article.class))).thenReturn(article);

        mockMvc.perform(post("/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(article)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name").value(articleName))
                        .andExpect(jsonPath("$.id").value(articleID));;
    }
    @Test
    void updateArticle_sendRequestWithUpdatedArticle_expectArticleTobeUpdatedAndStatusOk() throws Exception {
        int articleID = 1;
        String articleName= "test";
        String updatedArticleName = "Test Article";
        Article article = new Article();
        article.setId(articleID);
        article.setName(articleName);

        // Convert the article object to JSON string
        String articleJson = asJsonString(article);
        // Mock the service method
        when(articleService.updateArticle(eq(articleID), any(Article.class))).thenReturn(article);


        mockMvc.perform(post("/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(article)));

        article.setName(updatedArticleName);
        mockMvc.perform(put("/v1/articles/1").contentType(MediaType.APPLICATION_JSON).content(articleJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(articleID))
                .andExpect(jsonPath("$.name").value(updatedArticleName));
    }
    @Test
    void deleteArticleTest_sendDeleteRequestWithArticleIDForSavedArticle_expectArticleToBeDeleted() throws Exception {
        int articleId = 1 ;
        doNothing().when(articleService).deleteArticle(articleId);

        mockMvc.perform(
                delete("/v1/articles/1")
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteArticleTest_sendDeleteRequestArticleIDForNonSavedArticle_expectBadRequest() throws Exception {
        int articleId = 1 ;

        doThrow(ArticleNotFoundException.class).when(articleService).deleteArticle(articleId);


        mockMvc.perform(
                        delete("/v1/articles/1")
                )
                .andExpect(status().isBadRequest());
    }
}