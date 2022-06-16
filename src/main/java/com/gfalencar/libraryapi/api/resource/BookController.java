package com.gfalencar.libraryapi.api.resource;

import com.gfalencar.libraryapi.api.dto.BookDTO;
import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {
//Criando a rota de POST

    private BookService service;

    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper mapper) {
        this.service = service;
        this.modelMapper = mapper;
    }

    @PostMapping // Anotação que se trata de um método POST
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO dto ){
        Book entity = modelMapper.map(dto,Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }
}
