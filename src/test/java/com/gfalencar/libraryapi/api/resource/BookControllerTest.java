package com.gfalencar.libraryapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfalencar.libraryapi.api.dto.BookDTO;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
//    Primeiro teste - Post.
    @Test
    @DisplayName("Deve criar um livro com sucesso!")
    public void createBookTest() throws Exception{
//  Scenario
        BookDTO dto = BookDTO.builder().author("Artur").title("As Aventuras").isbn("001").build();
        Book savedBook = Book.builder().id(1l).author("Artur").title("As Aventuras").isbn("001").build();
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
}
