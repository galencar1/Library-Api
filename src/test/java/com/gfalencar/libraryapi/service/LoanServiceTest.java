package com.gfalencar.libraryapi.service;

import com.gfalencar.libraryapi.api.dto.LoanFilterDTO;
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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    private LoanRepository repository;
    private LoanService loanService;
    /***********************************************************************************************************************/
    public static Loan createLoan(){
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        return Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
    }
    /***********************************************************************************************************************/
    @BeforeEach
    public void setUp(){
        this.loanService = new LoanServiceImpl(repository);
    }
    /***********************************************************************************************************************/
    @Test
    @DisplayName("Deve salvar um empr??stimo")
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
@DisplayName("Deve lan??ar erro de neg??cio ao salvar um empr??stimo com um livro j?? emprestado")
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
/***********************************************************************************************************************/
    @Test
    @DisplayName("Deve obter as informa????es de um empr??stimo pelo ID")
    public void getLoanDetailsTest(){
//        scenario
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));
//        executor
        Optional<Loan> result = loanService.getById(id);
//        verify
        Assertions.assertThat(result.isPresent()).isTrue();
        Assertions.assertThat(result.get().getId()).isEqualTo(id);
        Assertions.assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        Assertions.assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        Assertions.assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(repository).findById(id);
    }
/***********************************************************************************************************************/
    @Test
    @DisplayName("Deve atualizar um empr??stimo")
    public void updateLoanTest(){
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);
        loan.setReturned(true);

        Mockito.when(repository.save(loan)).thenReturn(loan);

        Loan updatedLoan = loanService.update(loan);

        Assertions.assertThat(updatedLoan.getReturned()).isTrue();
        Mockito.verify(repository).save(loan);
    }
/*******************************************************************************************************************/
    @Test
    @DisplayName("Deve filtrar empr??stimos pelas propriedades")
    public void findLoanTest(){
    //scenario
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("Fulano").isbn("321").build();

        Loan loan = createLoan();
        loan.setId(1L);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>( lista, pageRequest, lista.size());
        Mockito.when(repository.findByBookIsbnOrCustomer(
                Mockito.anyString(),
                Mockito.anyString(),
                        Mockito.any(PageRequest.class)))
                .thenReturn(page);
    //executor
        Page<Loan> result = loanService.find(loanFilterDTO , pageRequest);
    //Verify
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(lista);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);


    }
}
