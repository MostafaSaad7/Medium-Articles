package com.vodafone.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="Author")
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    

}
