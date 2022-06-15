package com.gfalencar.libraryapi.api.resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class) // Spring criará um mini contexto para rodar o teste
@ActiveProfiles("test") // Roda apenas em ambiente de teste
@WebMvcTest // Testes unitários - Apenas para testar o comportamento da API - Os métodos implementados
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired // Injeta dependência
    MockMvc mvc; // Simula como se fosse uma requisição para API.

//    Primeiro teste - Post.
    @Test
    @DisplayName("Deve criar um livro com sucesso!")
    public void createBookTest(){

    }

//    Teste erro Criação de livro.
    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficiente para criação do livro!")
    public void createInvalidBookTest(){

    }
}
