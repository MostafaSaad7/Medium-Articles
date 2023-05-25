package com.vodafone.contoller;

import java.util.Arrays;
import java.util.List;


import com.vodafone.model.Author;


import com.vodafone.service.AuthorService;
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


@ExtendWith({SpringExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    private String asJsonString(Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void getAuthorByIDTest_SendGetRequest_shouldReturnAuthorWithGivenID() throws Exception {
        int authorId = 1;
        String authorName= "test";
        Author author = new Author();
        author.setName(authorName);
        author.setId(authorId);
        when(authorService.getAuthorById(authorId)).thenReturn(author);

        mockMvc.perform(get("/v1/authors/"+authorId).contentType(MediaType.APPLICATION_JSON_VALUE)).
                andExpect(status().isOk()).andExpect(jsonPath("$.name").value(authorName));

        verify(authorService, times(1)).getAuthorById(anyInt());
        verifyNoMoreInteractions(authorService);
    }
    @Test
    void getAuthorsTest_sendGetRequest_shouldReturnAllArticles() throws Exception {

        // Arrange
        // Prepare test data
        Author author1 = new Author();
        Author author2 = new Author();
        List<Author> authorList = Arrays.asList(author1, author2);
        // Configure mock service
        when(authorService.getAllAuthors(anyInt(), anyInt())).thenReturn(authorList);

        //Act + Assertion
        // Perform GET request
        mockMvc.perform(get("/v1/authors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(authorList.size()));

        // Verify mock service interaction (Assertion)
        verify(authorService, times(1)).getAllAuthors(anyInt(), anyInt());
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void addAuthorTest_sendRequestIncludingAuthorInBody_expectStatusCodeCreated() throws Exception {
        int authorId = 1;
        String authorName= "test";
        Author author = new Author();
        author.setName(authorName);
        author.setId(authorId);
        when(authorService.addAuthor(any(Author.class))).thenReturn(author);

        mockMvc.perform(post("/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(author)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(authorName))
                .andExpect(jsonPath("$.id").value(authorId));;
    }

    @Test
    void deleteAuthorByIdTest_sendDeleteRequestWithAuthorIDForSavedArticle_expectAuthorToBeDeleted() throws Exception {
        int articleId = 1 ;
        doNothing().when(authorService).deleteAuthorById(articleId);
        mockMvc.perform(delete("/v1/authors/1")).
                andExpect(status().isNoContent());
    }

    @Test
    void updateAuthorTest_sendRequestWithUpdatedArticle_expectArticleTobeUpdatedAndStatusOk() throws Exception {
        int authorID = 1;
        String authorName= "test";
        String updatedAuthorName = "Test Article";
        Author author = new Author();
        author.setId(authorID);
        author.setName(authorName);

        // Convert the article object to JSON string
        String authorJson = asJsonString(author);
        // Mock the service method
        when(authorService.updateAuthor(eq(authorID), any(Author.class))).thenReturn(author);


        mockMvc.perform(post("/v1/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(author)));

        author.setName(updatedAuthorName);
        mockMvc.perform(put("/v1/authors/1").contentType(MediaType.APPLICATION_JSON).content(authorJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authorID))
                .andExpect(jsonPath("$.name").value(updatedAuthorName));
    }
}