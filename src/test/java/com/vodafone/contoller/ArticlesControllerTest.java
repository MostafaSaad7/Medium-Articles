package com.vodafone.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vodafone.model.Article;
import com.vodafone.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import javax.swing.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

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
    void addArticle() throws Exception {
        Article article = new Article();
        article.setName("test");
        article.setId(1);
        when(articleService.addArticle(any(Article.class))).thenReturn(article);

        mockMvc.perform(post("/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(article)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name").value("test"))
                        .andExpect(jsonPath("$.id").value(1));;
    }
    @Test
    void updateArticle() throws Exception {
        // Create a sample article
        Article article = new Article();
        article.setId(1);
        article.setName("Test");

        // Convert the article object to JSON string
        String articleJson = asJsonString(article);

        // Mock the service method
        when(articleService.updateArticle(eq(1), any(Article.class))).thenReturn(article);

        mockMvc.perform(post("/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(article)));

        article.setName("Test Article");
        // Perform the PUT request to update the article
        mockMvc.perform(put("/v1/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Article"));
    }
    @Test
    void deleteArticle() throws Exception {
        // Mock the service method
//        doThrow(new RuntimeException()).when(articleService).deleteArticle(1);
        doNothing().when(articleService).deleteArticle(1);

        // Perform the DELETE request to delete the article
        mockMvc.perform(delete("/v1/articles/1"))
                .andExpect(status().isNoContent());
    }
}