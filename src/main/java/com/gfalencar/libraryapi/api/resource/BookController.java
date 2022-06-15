package com.gfalencar.libraryapi.api.resource;

import com.gfalencar.libraryapi.api.dto.BookDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {
//Criando a rota de POST
    @PostMapping // Anotação que se trata de um método POST
    public BookDTO create(){
        return null;
    }
}
