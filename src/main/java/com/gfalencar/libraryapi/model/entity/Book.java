package com.gfalencar.libraryapi.model.entity;

import lombok.*;
import javax.persistence.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Book {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String author;

    @Column
    private String isbn;

    @OneToMany( mappedBy = "book")
    private List<Loan> loans;
}
