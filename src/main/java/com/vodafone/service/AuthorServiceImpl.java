package com.vodafone.service;

import com.vodafone.errorhandlling.AuthorNotFoundException;
import com.vodafone.model.Author;
import com.vodafone.repository.AuthorRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class AuthorServiceImpl implements AuthorService
{
    @Autowired
    AuthorRepository authorRepository;
    
    @Override
    public Author getAuthorById(Integer id) 
    {
        return authorRepository.findById(id).get();
    }

    @Override
    public Author addAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public List<Author> getAllAuthors(int page, int pageSize) {
    List<Author> allAuthors = new ArrayList<>();
    Page<Author> authorPage;
    page = Math.max(0, page);
    do {
        authorPage = authorRepository.findAll(PageRequest.of(page, pageSize));
        List<Author> authorList = authorPage.getContent();
        allAuthors.addAll(authorList);
        page++;
    } while (page < authorPage.getTotalPages());

    return allAuthors;
}


    @Override
    public List<Author> deleteAuthorByName(String authorName) {
        List<Author> authors = authorRepository.findAllByName(authorName);

        if (authors.isEmpty()) {
            throw new AuthorNotFoundException("No authors found with the given name");
        }

        authorRepository.deleteAllByName(authorName);

        return authors;
    }

    @Override
    public void deleteAuthorById(Integer authorId) {
        Optional<Author> author = authorRepository.findById(authorId);

        if (author.isPresent()) {
            authorRepository.deleteById(authorId);
        } else {
            throw new AuthorNotFoundException("Can't find the author to delete");
        }
    }

    @Override
    public Author updateAuthor(Integer authorId, Author author) {
        Optional<Author> optionalAuthor = authorRepository.findById(authorId);

        if (optionalAuthor.isPresent()) {
            Author existingAuthor = optionalAuthor.get();
            existingAuthor.setName(author.getName());
            return authorRepository.save(existingAuthor);
        } else {
            throw new AuthorNotFoundException("Author with ID " + authorId + " not found");
        }
    }



}
