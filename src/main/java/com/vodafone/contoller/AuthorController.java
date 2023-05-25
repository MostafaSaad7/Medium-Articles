package com.vodafone.contoller;

import java.util.List;

import com.vodafone.model.Author;
import com.vodafone.service.AuthorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/v1")
public class AuthorController {

    @Autowired
    @Qualifier("authorServiceImpl")
    private AuthorService authorService;

    @GetMapping(value = "/authors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Author> getAuthorById(@PathVariable(name = "id") Integer id){
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @GetMapping(value = "/authors", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Author>> getAuthors(
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize
    ) {
        List<Author> authors;

            authors = authorService.getAllAuthors(page != null ? page : 0, pageSize != null ? pageSize : 10);

        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @PostMapping(value = "/authors" , produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Author> addAuthor(@RequestBody Author author){
        return new ResponseEntity<>(authorService.addAuthor(author), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/authors/{authorName}")
    public ResponseEntity<List<Author>> deleteAuthorByName(@PathVariable("authorName") String authorName) {
        List<Author> authorList= authorService.deleteAuthorByName(authorName);
        return new ResponseEntity<>(authorList,HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "authors/id/{authorId}")
    public ResponseEntity<String> deleteAuthorById(@PathVariable("authorId") int authorId) {
        authorService.deleteAuthorById(authorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(value = "/authors/{authorId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Author> updateAuthor(@PathVariable(name = "authorId") Integer authorId, @RequestBody Author author) {
        Author updatedAuthor = authorService.updateAuthor(authorId, author);
        return new ResponseEntity<>(updatedAuthor, HttpStatus.OK);
    }

}
