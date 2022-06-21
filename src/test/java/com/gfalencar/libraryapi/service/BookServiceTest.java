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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
/*******************************************************************************************************************/
    @MockBean
    BookRepository repository;

    BookService service;
/*******************************************************************************************************************/
    private Book createValidBook(){
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }
/*******************************************************************************************************************/
    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }
/*******************************************************************************************************************/
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
/*******************************************************************************************************************/
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
/*******************************************************************************************************************/
    @Test
    @DisplayName("Deve obter um livro por ID")
    public void getByIdTest(){
//  scenario
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
//  executor
        Optional<Book> foundBook = service.getById(id);
//  verificator
        Assertions.assertThat( foundBook.isPresent() ).isTrue();
        Assertions.assertThat( foundBook.get().getId() ).isEqualTo(id);
        Assertions.assertThat( foundBook.get().getAuthor() ).isEqualTo( book.getAuthor() );
        Assertions.assertThat( foundBook.get().getTitle() ).isEqualTo( book.getTitle() );
        Assertions.assertThat( foundBook.get().getIsbn() ).isEqualTo( book.getIsbn() );

    }
/*******************************************************************************************************************/
    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por ID quando ele não existe")
    public void bookNotFoundByIdTest(){
//  scenario
        Long id = 1L;
        Mockito.when(repository.findById(id)).thenReturn( Optional.empty() );
//  executor
        Optional<Book> notFoundBook = service.getById(id);
//  verificator
        Assertions.assertThat( notFoundBook.isPresent() ).isFalse();
    }
/*******************************************************************************************************************/
    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
//  scenario
       Book book = Book.builder().id(1L).build();
//  executor
        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(book) );
//  verify
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }
/*******************************************************************************************************************/
    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente ")
    public void deleteInvalidBookTest(){
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        Mockito.verify(repository, Mockito.never()).delete(book);
    }
/*******************************************************************************************************************/
    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente ")
    public void updateInvalidBookTest(){
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));

        Mockito.verify(repository, Mockito.never()).save(book);
    }
/*******************************************************************************************************************/
    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest(){
//  scenario
        Long id = 1L;
        //Livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        //Livro atualizado
        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

//  executor
        Book book = service.update(updatingBook);

//  verify
        Assertions.assertThat(book.getId()).isEqualTo(updatedBook.getId());
        Assertions.assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        Assertions.assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        Assertions.assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }
/*******************************************************************************************************************/
    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest(){
//scenario
        Book book = createValidBook();
//        book.setId(1L);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);
//executor
        Page<Book> result = service.find(book, pageRequest);
//Verify
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(lista);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);


    }
/*******************************************************************************************************************/
    @Test
    @DisplayName("Deve obter um livro pelo ISBN")
    public void getBookByIsbnTest(){
//  scenario
        String isbn = "123";
        Mockito.when(repository.findByIsbn(isbn)).thenReturn( Optional.of( Book.builder().id(1L).isbn(isbn ).build() ) );
//  executor
        Optional<Book> book = service.getBookByIsbn(isbn);
//  verify
        Assertions.assertThat(book.isPresent()).isTrue();
        Assertions.assertThat(book.get().getId()).isEqualTo(1L);
        Assertions.assertThat(book.get().getIsbn()).isEqualTo(isbn);

        Mockito.verify(repository, Mockito.times(1)).findByIsbn(isbn);
    }
}
