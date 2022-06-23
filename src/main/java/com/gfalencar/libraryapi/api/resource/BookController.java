package com.gfalencar.libraryapi.api.resource;

import com.gfalencar.libraryapi.api.dto.BookDTO;
import com.gfalencar.libraryapi.api.dto.LoanDTO;
import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.model.entity.Loan;
import com.gfalencar.libraryapi.service.BookService;
import com.gfalencar.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@Api("Book API")

public class BookController {
//Criando a rota de POST

    private final BookService service;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    public BookController(BookService service, ModelMapper modelMapper, LoanService loanService) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.loanService = loanService;
    }

    @PostMapping // Anotação que se trata de um método POST
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create a Book")
    public BookDTO create(@RequestBody @Valid BookDTO dto ){
        Book entity = modelMapper.map(dto,Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Delete a Book By ID")
    public void delete(@PathVariable Long id){
        Book book = service.getById(id)
                .orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND ) );
        service.delete(book);
    }

    @GetMapping("{id}") // anotação para tratar de um método Get.
    @ApiOperation("Obtains a Book Detail By Id")
    public BookDTO get(@PathVariable Long id){
        return service
                .getById(id)
                .map( book ->  modelMapper.map(book, BookDTO.class) )
                .orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND ) );
    }

    @PutMapping("{id}")
    @ApiOperation("Updates a book's data by id")
    public BookDTO update(@PathVariable Long id, @RequestBody @Valid BookDTO dto){
        return service.getById(id).map( book -> {
            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book = service.update(book);
            return modelMapper.map(book, BookDTO.class);

        }).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
    }

    @GetMapping
    @ApiOperation("Browse a Book")
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest){
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDTO> list = result.getContent()
                .stream()
                .map( entity -> modelMapper.map(entity, BookDTO.class) )
                .collect( Collectors.toList() );

        return new PageImpl<BookDTO>( list, pageRequest , result.getTotalElements() );

    }

    @GetMapping("{id}/loans")
    @ApiOperation("See loan data by book ID")
    public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable){
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoansByBook(book, pageable);

        List<LoanDTO> list = result.getContent()
                .stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(list, pageable, result.getTotalElements());
    }
}
