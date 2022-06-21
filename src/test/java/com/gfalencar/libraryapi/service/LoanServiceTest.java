package com.gfalencar.libraryapi.service;

import com.gfalencar.libraryapi.exception.BusinessException;
import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.model.entity.Loan;
import com.gfalencar.libraryapi.model.repository.LoanRepository;
import com.gfalencar.libraryapi.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    private LoanRepository repository;
    private LoanService loanService;

    @BeforeEach
    public void setUp(){
        this.loanService = new LoanServiceImpl(repository);
    }
    /***********************************************************************************************************************/
    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest(){
        Book book = Book.builder().id(1L).build();
        /**************************************************/
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
        /**************************************************/
        Loan savedLoan = Loan.builder()
                .id(1L)
                .loanDate(LocalDate.now())
                .customer("Fulano")
                .book(book)
                .build();
        /**************************************************/
        Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        Mockito.when( repository.save(savingLoan) ).thenReturn(savedLoan);

        Loan loan = loanService.save(savingLoan);

        Assertions.assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        Assertions.assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        Assertions.assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        Assertions.assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }
/***********************************************************************************************************************/
@Test
@DisplayName("Deve lançar erro de negócio ao salvar um empréstimo com um livro já emprestado")
public void loanedBookSaveTest(){
    Book book = Book.builder().id(1L).build();
    /**************************************************/
    Loan savingLoan = Loan.builder()
            .book(book)
            .customer("Fulano")
            .loanDate(LocalDate.now())
            .build();

    Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(true);
    /**************************************************/
    Throwable exception = Assertions.catchThrowable(() -> loanService.save(savingLoan));
    /**************************************************/

    Assertions.assertThat(exception)
            .isInstanceOf(BusinessException.class)
            .hasMessage("Book already loaned");

    Mockito.verify(repository, Mockito.never()).save(savingLoan);
}
}
