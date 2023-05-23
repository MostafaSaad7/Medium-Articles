package com.vodafone.service;

import com.vodafone.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    Author getAuthorById(Integer id);
    Author addAuthor(Author author);
    List<Author> getAllAuthors(int pageSize,int page);

    List<Author> deleteAuthorByName(String AuthorName);

    void deleteAuthorById(Integer authorId);

    Author updateAuthor(Integer authorId , Author author);
}
