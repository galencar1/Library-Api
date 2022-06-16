package com.gfalencar.libraryapi.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//DTO -Se trata de uma classe de atributo simples para representar o JSON
@Getter
@Setter
@Builder
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
}
