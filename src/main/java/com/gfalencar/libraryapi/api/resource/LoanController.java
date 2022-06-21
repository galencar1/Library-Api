package com.gfalencar.libraryapi.api.resource;

import com.gfalencar.libraryapi.api.dto.LoanDTO;
import com.gfalencar.libraryapi.api.dto.LoanFilterDTO;
import com.gfalencar.libraryapi.api.dto.ReturnedLoanDTO;
import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.model.entity.Loan;
import com.gfalencar.libraryapi.service.BookService;
import com.gfalencar.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor // Cria construtor as variáveis
public class LoanController {
    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    /*******************************************************************************************************************/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto){
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(  HttpStatus.BAD_REQUEST, "Book not found for passed ISBN" ));
        Loan entity = Loan.builder()
                    .book(book)
                    .customer(dto.getCustomer())
                    .loanDate(LocalDate.now())
                    .build();

        entity = loanService.save(entity);

        return entity.getId();
    }
/*****************************************************************************************************************/
    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){

        Loan loan = loanService.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
        loan.setReturned(dto.isReturned());

        loanService.update(loan);
    }
    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest){
        Page<Loan> result = loanService.find(dto, pageRequest);
        return null;
    }
}
