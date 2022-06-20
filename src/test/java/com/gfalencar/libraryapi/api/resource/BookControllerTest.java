package com.gfalencar.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfalencar.libraryapi.api.dto.BookDTO;
import com.gfalencar.libraryapi.exception.BusinessException;
import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.awt.print.Pageable;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class) // Spring criará um mini contexto para rodar o teste
@ActiveProfiles("test") // Roda apenas em ambiente de teste
@WebMvcTest // Testes unitários - Apenas para testar o comportamento da API - Os métodos implementados
@AutoConfigureMockMvc
public class BookControllerTest {
/*****************************************************************************************/
//    Definindo a rota de acesso a API para efetuar as requisições
    static String BOOK_API = "/api/books";
/*******************************************************************************************/
    @Autowired // Injeta dependência
    MockMvc mvc; // Simula como se fosse uma requisição para API.

    @MockBean
    BookService service;
/*******************************************************************************************/
//Método para criação de um livro.
    private BookDTO createNewBook(){
        return BookDTO.builder()
                .author("Artur")
                .title("As aventuras")
                .isbn("001")
                .build();
    }
/*************************************************************************************/
//    Primeiro teste - Post.
    @Test
    @DisplayName("Deve criar um livro com sucesso!")
    public void createBookTest() throws Exception{
//  Scenario
        BookDTO dto = createNewBook();
        Book savedBook = Book.builder().id(1L).author("Artur").title("As aventuras").isbn("001").build();
        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
        String json = new ObjectMapper().writeValueAsString(dto); // Transforma um objeto em JSON
//  Define um requisição com Mock
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API) // Primeiro parametro - ROTA
                .contentType(MediaType.APPLICATION_JSON) // Tipo de contéudo que será passado. Nosso caso(JSON)
                .accept(MediaType.APPLICATION_JSON) // Tipo de contéudo aceito pelo servidor.
                .content(json);// Conteúdo a ser passado. No nosso caso do tipo JSON.
//  executor
//  Efetuando a Requisição
        mvc
           .perform(request) // Executa a requsição criado acima.
//  verificator
           .andExpect(status().isCreated()) // Método andExpect() -> Aqui passamos as nossas assertivas. OU seja o que estamos esperando.
               .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty()) // Espero que ele retorne o id populado. Não pode estar vazio.
           .andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
           .andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));
    }

//    Teste erro Criação de livro.
    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficiente para criação do livro!")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isBadRequest() )
                .andExpect(jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar erro com ISBN já utilizado por outro.")
    public void createBookWithDuplicatedIsbn() throws Exception {
//      scenario
        BookDTO dto = createNewBook();

        String json = new ObjectMapper().writeValueAsString(dto);
        String msgErro = "ISBN já cadastrado!";

        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(msgErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
//      executor
        mvc.perform(request)
//      verify
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(msgErro));

    }

    @Test
    @DisplayName("Deve obter informações de um livro.")
    public void getBookDetailsTest() throws Exception {
//  scenario
        Long id = 1L;
        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));
//  executor(when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);
//  verify
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()))
    ;
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir")
    public void bookNotFoundTest() throws Exception{

        BDDMockito.given( service.getById(Mockito.anyLong()) ).willReturn(Optional.empty() );
//  executor
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
//  verify
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro ")
    public void deleteBookTest() throws Exception{
//  scenario
        Long id = 1L;
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(Book.builder().id(id).build()));
//  executor
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);
//  verify
        mvc
                .perform( request )
                .andExpect(status().isNoContent() );

    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar o livro para deletar")
    public void deleteInexistentBookTest() throws Exception{
//  scenario
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
//  executor
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1L))
                .accept(MediaType.APPLICATION_JSON);
//  verify
        mvc
                .perform( request )
                .andExpect(status().isNotFound());

    }
    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception{
//  scenario
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        //Atualizando o livro
        Book updatingBook = Book.builder().id(id).title("some title").author("some author").isbn("001").build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(updatingBook));
        //Atualizado
        Book updatedBook = Book.builder().id(id ).author("Artur").title("As aventuras").isbn("001").build();
        BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);
//  executor
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
//  verify
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar um 404 ao tentar atualizar um livro inexistente")
    public void updateInexistentBookTest() throws Exception{
//  scenario
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given(service.getById(Mockito.anyLong()) ).willReturn(Optional.empty());
//  executor
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+ 1L))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
//  verify
        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void findBooksTest() throws Exception {
        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given( service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)) )
                .willReturn(new PageImpl<Book>( Arrays.asList(book), PageRequest.of(0, 100), 1 ) );


        String querysString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(querysString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform( request )
                .andExpect( status().isOk() )
                .andExpect( jsonPath("content", Matchers.hasSize(1)) )
                .andExpect( jsonPath("totalElements").value(1) )
                .andExpect( jsonPath("pageable.pageSize").value(100) )
                .andExpect( jsonPath("pageable.pageNumber").value(0) )
                ;
    }
}
