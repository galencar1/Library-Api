package com.gfalencar.libraryapi.service;

import com.gfalencar.libraryapi.exception.BusinessException;
import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.repository.BookRepository;
import com.gfalencar.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;
    @MockBean
    BookRepository repository;

    private Book createValidBook(){
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    public void savedBookTest(){
//scenario
        Book book = createValidBook();

        Mockito.when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(false);

        Mockito.when(repository.save(book)).thenReturn(
                Book.builder()
                .id(1L)
                .isbn("123")
                .title("As aventuras")
                .author("Fulano").build());
//executor
        Book savedBook = service.save(book);
//verify
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("123");
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado.")
    public void shouldNotSaveABookWithDuplicatedISBN(){
//  scenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
//  executor
        Throwable exception = Assertions.catchThrowable (  () -> service.save(book)  );
//  verify
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN já cadastrado!");

        Mockito.verify(repository, Mockito.never()).save(book);
    }
}
