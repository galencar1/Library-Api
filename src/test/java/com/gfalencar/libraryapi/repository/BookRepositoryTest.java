package com.gfalencar.libraryapi.repository;

import com.gfalencar.libraryapi.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager; // Cria um cenário.

    @Autowired
    BookRepository repository;

    private Book createValidBook(){
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o ISBN informado")
    public void returnTrueWhenIsbnExists(){
//  scenario
        String isbn = "123";
        Book book = Book.builder().title("As aventuras").author("Fulano").isbn(isbn).build();
        entityManager.persist(book);
//  executor
        boolean exists = repository.existsByIsbn(isbn);
//  verify
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base com o ISBN informado")
    public void returnFalseWhenIsbnDoesntExist(){
//  scenario
        String isbn = "123";

//  executor
        boolean exists = repository.existsByIsbn(isbn);
//  verify
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por ID")
    public void findByIdTest(){
//  scenario
        Book book = createValidBook();
        entityManager.persist(book);
//  executor
        Optional<Book> foundBook = repository.findById(book.getId());
//  verify
        Assertions.assertThat(foundBook.isPresent()).isTrue();
    }

}
