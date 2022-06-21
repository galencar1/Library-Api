package com.gfalencar.libraryapi.repository;

import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.model.entity.Loan;
import com.gfalencar.libraryapi.model.repository.LoanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;

import java.time.LocalDate;

import static com.gfalencar.libraryapi.repository.BookRepositoryTest.createValidBook;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private LoanRepository repository;

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
    public void existsByBookAndNotReturnedTest(){
//scenario
        Book book = createValidBook();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);
//  executor
        boolean exists = repository.existsByBookAndNotReturned(book);

//  verify
        Assertions.assertThat(exists).isTrue();
    }
}
