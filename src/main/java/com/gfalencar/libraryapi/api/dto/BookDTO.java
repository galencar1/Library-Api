package com.gfalencar.libraryapi.api.dto;

import lombok.*;

//DTO -Se trata de uma classe de atributo simples para representar o JSON
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
}
