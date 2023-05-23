package com.vodafone.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name="Article")
@Data
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String author;
    private int authorId;

    @JsonProperty("_links")
    @Transient
    private List<Links> links;
    @Override
    public String toString() {
        return "Article [id=" + id + ", name=" + name + ", author=" + author + ", authorId=" + authorId + ", links="
                + links + "]";
    }

}
