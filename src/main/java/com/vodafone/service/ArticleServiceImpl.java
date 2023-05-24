package com.vodafone.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vodafone.errorhandlling.ArticleNotFoundException;
import com.vodafone.errorhandlling.AuthorNotFoundException;
import com.vodafone.errorhandlling.DuplicatedIDException;
import com.vodafone.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vodafone.contoller.ArticlesController;
import com.vodafone.contoller.AuthorController;
import com.vodafone.errorhandlling.NotFoundException;
import com.vodafone.model.*;
import com.vodafone.repository.ArticleRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService
{
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Override
    public List<Article> getAllArticles(int page, int pageSize) {
        List<Article> allArticles = new ArrayList<>();
        Page<Article> articlePage;
        page = Math.max(0, page);
        do {
            articlePage = articleRepository.findAll(PageRequest.of(page, pageSize));
            List<Article> arts = articlePage.getContent();
            arts.forEach(this::addLinks);
            allArticles.addAll(arts);
            page++;
        } while (page < articlePage.getTotalPages());

        return allArticles;
    }



    @Override
    public Article getArticleById(Integer id)
    {
        var optionalArticle = articleRepository.findById(id);
        if(optionalArticle.isEmpty())
        {
            throw new NotFoundException(String.format("Article by id: %s not found", id));
        }
        var article = optionalArticle.get();
        addLinks(article);
        return article;
    }

    @Override
    public List<Article> getArticlesByAuthorName(String authorName)
    {
        var arts = articleRepository.findByAuthorContains(authorName);
        for(var article : arts)
        {
            addLinks(article);
        }
        return arts;
    }

    @Override
    public Article addArticle(Article article)
    {
        if  (article.getId() != null && articleRepository.existsById(article.getId()))
        {
            throw new DuplicatedIDException("Duplicated id ");
        }
        else if(!authorRepository.existsById(article.getAuthorId()) || !authorRepository.findById(article.getAuthorId()).get().getName().equals(article.getAuthor()))
        {
            throw new AuthorNotFoundException("Please provide a valid author info");
        }
        return articleRepository.save(article);
    }

    @Override
    public void deleteArticle(Integer id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);

        if (optionalArticle.isPresent()) {
            articleRepository.deleteById(id);
        } else {
            throw new ArticleNotFoundException("Article with ID " + id + " not found");
        }
    }


    @Override
    public Article updateArticle(Integer id, Article article) {
        Optional<Article> optionalArticle = articleRepository.findById(id);

        if (optionalArticle.isPresent()) {
            Article existingArticle = optionalArticle.get();
            existingArticle.setAuthor(article.getAuthor());
            existingArticle.setName(article.getName());
            existingArticle.setAuthorId(article.getAuthorId());
            return articleRepository.save(existingArticle);
        } else {
            throw new ArticleNotFoundException("Article with ID " + id + " not found");
        }
    }

    private void addLinks(Article article){
        List<Links> links = new ArrayList<>();
        Links self = new Links();

        Link selfLink = linkTo(methodOn(ArticlesController.class)
                .getArticle(article.getId())).withRel("self");

        self.setRel("self");
        self.setHref(selfLink.getHref());

        Links authorLink = new Links();
        Link authLink = linkTo(methodOn(AuthorController.class)
                .getAuthorById(article.getAuthorId())).withRel("author");
        authorLink.setRel("author");
        authorLink.setHref(authLink.getHref());

        links.add(self);
        links.add(authorLink);
        article.setLinks(links);
    }
}
