package com.gfalencar.libraryapi.api.resource;

import com.gfalencar.libraryapi.api.dto.LoanDTO;
import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.model.entity.Loan;
import com.gfalencar.libraryapi.service.BookService;
import com.gfalencar.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor // Cria construtor as variáveis
public class LoanController {
    private final LoanService loanService;
    private final BookService bookService;

/*******************************************************************************************************************/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto){
        Book book = bookService.getBookByIsbn(dto.getIsbn()).get();
        Loan entity = Loan.builder()
                    .book(book)
                    .customer(dto.getCustomer())
                    .loanDate(LocalDate.now())
                    .build();

        entity = loanService.save(entity);

        return entity.getId();
    }

}
