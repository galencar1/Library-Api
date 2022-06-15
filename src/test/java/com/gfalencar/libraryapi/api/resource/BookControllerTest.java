package com.gfalencar.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class) // Spring criará um mini contexto para rodar o teste
@ActiveProfiles("test") // Roda apenas em ambiente de teste
@WebMvcTest // Testes unitários - Apenas para testar o comportamento da API - Os métodos implementados
@AutoConfigureMockMvc
public class BookControllerTest {

//    Definindo a rota de acesso a API para efetuar as requisições
    static String BOOK_API = "/api/books";

    @Autowired // Injeta dependência
    MockMvc mvc; // Simula como se fosse uma requisição para API.

//    Primeiro teste - Post.
    @Test
    @DisplayName("Deve criar um livro com sucesso!")
    public void createBookTest() throws Exception{
//  Scenario
        String json = new ObjectMapper().writeValueAsString(null); // Transforma um objeto em JSON
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
           .andExpect(MockMvcResultMatchers.status().isCreated()) // Método andExpect() -> Aqui passamos as nossas assertivas. OU seja o que estamos esperando.
           .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty()) // Espero que ele retorne o id populado. Não pode estar vazio.
           .andExpect(MockMvcResultMatchers.jsonPath("title").value("Meu Livro"))
           .andExpect(MockMvcResultMatchers.jsonPath("author").value("Autor"))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("12121212"));
    }

//    Teste erro Criação de livro.
    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficiente para criação do livro!")
    public void createInvalidBookTest(){

    }
}
