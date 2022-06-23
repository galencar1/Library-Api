package com.gfalencar.libraryapi.repository;

import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.model.entity.Loan;
import com.gfalencar.libraryapi.model.repository.LoanRepository;
import com.gfalencar.libraryapi.service.EmailService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

import static com.gfalencar.libraryapi.repository.BookRepositoryTest.createValidBook;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private LoanRepository repository;

    @MockBean
    private EmailService emailService;

    public Loan createAndPersistLoan(LocalDate loanDate){
        Book book = createValidBook();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(loanDate).build();
        entityManager.persist(loan);

        return loan;
    }

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
    public void existsByBookAndNotReturnedTest(){
//scenario
        Loan loan = createAndPersistLoan(LocalDate.now());
        Book book = loan.getBook();
//  executor
        boolean exists = repository.existsByBookAndNotReturned(book);

//  verify
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar empréstimo pelo ISBN do livro ou Customer")
    public void findByBookIsbnOrCustomerTest(){
//scenario
        Loan loan = createAndPersistLoan(LocalDate.now());

        Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Fulano", PageRequest.of(0, 10));

        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getContent()).contains(loan);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);

    }
    @Test
    @DisplayName("Deve obter empréstimos cuja data emprestimo for menor ou igual a tres dias atras e não retornados")
    public void findByLoanDateLessThanAndNotReturnedTest(){
        Loan loan = createAndPersistLoan(LocalDate.now().minusDays(5));

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        Assertions.assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("Deve retornar vazio quando não houver emprestimos atrasados")
    public void notFindByLoanDateLessThanAndNotReturnedTest(){
        Loan loan = createAndPersistLoan(LocalDate.now());

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        Assertions.assertThat(result).isEmpty();
    }
}
