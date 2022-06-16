package com.gfalencar.libraryapi.api.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

//DTO -Se trata de uma classe de atributo simples para representar o JSON
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String author;
    @NotEmpty
    private String isbn;
}
