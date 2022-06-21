package com.gfalencar.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfalencar.libraryapi.api.dto.LoanDTO;
import com.gfalencar.libraryapi.exception.BusinessException;
import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.model.entity.Loan;
import com.gfalencar.libraryapi.service.BookService;
import com.gfalencar.libraryapi.service.LoanService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";
    @Autowired
    MockMvc mvc; // Será utilizado para fazer as requisições
    @MockBean
    private BookService bookService;
    @MockBean
    private LoanService loanService;

/************************************************************************************************************************/
    @Test
    @DisplayName("deve realizar um emprèstimo")
    public void  createLoanTest() throws Exception{


        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();// Criado o Json com o DTO
        String json = new ObjectMapper().writeValueAsString(dto); //Transforma String acima em JSON

        //Após receber o JSON acima - Busca no service o livro com o ISBN informado
        Book createdBook = Book.builder().id(1L).isbn("123").build();
        BDDMockito.given( bookService.getBookByIsbn("123") ).willReturn( Optional.of(createdBook) );

        //Realiza o empréstimo com o livro retornado.
        Loan loan = Loan.builder().id(1L).customer("Fulano").book(createdBook).loanDate(LocalDate.now()).build();
        BDDMockito.given( loanService.save(Mockito.any(Loan.class)) ).willReturn( loan );

        //Requisição -- Recebe o JSON settado no inicio -- Salva ma variavel request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        //Verifica os dados recebidos e se a requisição foi CRIADA
        mvc.perform( request )
                .andExpect( status().isCreated() )
                .andExpect( content().string("1") );
    }
/************************************************************************************************************************/
    @Test
    @DisplayName("Deve retornar erro ao fazer empréstimo de um livro inexistente.")
    public void invalidIsbnCreateLoanTest() throws Exception {
//  scenario
        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given( bookService.getBookByIsbn("123") ).willReturn(Optional.empty() );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform( request )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", Matchers.hasSize(1)) )
                .andExpect( jsonPath("errors[0]").value("Book not found for passed ISBN") );
    }
/************************************************************************************************************************/
    @Test
    @DisplayName("Deve retornar erro ao fazer empréstimo de um livro emprestado.")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {
//  scenario
        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn("123").build();
        BDDMockito.given( bookService.getBookByIsbn("123") ).willThrow(new BusinessException("Book already loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform( request )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", Matchers.hasSize(1)) )
                .andExpect( jsonPath("errors[0]").value("Book already loaned") );
    }
/************************************************************************************************************************/
}
