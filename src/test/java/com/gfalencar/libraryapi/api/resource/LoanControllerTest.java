package com.gfalencar.libraryapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfalencar.libraryapi.api.dto.LoanDTO;
import com.gfalencar.libraryapi.api.dto.LoanFilterDTO;
import com.gfalencar.libraryapi.api.dto.ReturnedLoanDTO;
import com.gfalencar.libraryapi.exception.BusinessException;
import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.model.entity.Loan;
import com.gfalencar.libraryapi.service.BookService;
import com.gfalencar.libraryapi.service.EmailService;
import com.gfalencar.libraryapi.service.LoanService;
import com.gfalencar.libraryapi.service.LoanServiceTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";
/************************************************************************************************************************/
    @Autowired
    MockMvc mvc; // Ser?? utilizado para fazer as requisi????es
/************************************************************************************************************************/
    @MockBean
    private BookService bookService;
/************************************************************************************************************************/
    @MockBean
    private LoanService loanService;

    @MockBean
    private EmailService emailService;

/************************************************************************************************************************/
    @Test
    @DisplayName("deve realizar um empr??stimo")
    public void  createLoanTest() throws Exception{


        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").email("customer@email.com").build();// Criado o Json com o DTO
        String json = new ObjectMapper().writeValueAsString(dto); //Transforma String acima em JSON

        //Ap??s receber o JSON acima - Busca no service o livro com o ISBN informado
        Book createdBook = Book.builder().id(1L).isbn("123").build();
        BDDMockito.given( bookService.getBookByIsbn("123") ).willReturn( Optional.of(createdBook) );

        //Realiza o empr??stimo com o livro retornado.
        Loan loan = Loan.builder().id(1L).customer("Fulano").book(createdBook).loanDate(LocalDate.now()).build();
        BDDMockito.given( loanService.save(Mockito.any(Loan.class)) ).willReturn( loan );

        //Requisi????o -- Recebe o JSON settado no inicio -- Salva ma variavel request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        //Verifica os dados recebidos e se a requisi????o foi CRIADA
        mvc.perform( request )
                .andExpect( status().isCreated() )
                .andExpect( content().string("1") );
    }
/************************************************************************************************************************/
    @Test
    @DisplayName("Deve retornar erro ao fazer empr??stimo de um livro inexistente.")
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
    @DisplayName("Deve retornar erro ao fazer empr??stimo de um livro emprestado.")
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
    @Test
    @DisplayName("Deve retornar um livro")
    public void returnBookTest() throws Exception {
//scenario
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        Loan loan = Loan.builder().id(1L).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(  Optional.of(loan) );

        String json = new ObjectMapper().writeValueAsString(dto);

        mvc.perform(
                MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk());


        Mockito.verify(loanService, Mockito.times(1) ).update(loan);
//executor
//verify
    }

/************************************************************************************************************************/
    @Test
    @DisplayName("Deve retornar 404 quando tentar devolver um livro inexistente")
    public void returnInexistentBookTest() throws Exception {
//scenario
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
//        Loan loan = Loan.builder().id(1L).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(  Optional.empty() );


        mvc.perform(
                MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isNotFound());

        //        Mockito.verify(loanService, Mockito.times(1) ).update(loan);
    }
/************************************************************************************************************************/
    @Test
    @DisplayName("Deve filtrar empr??stimos")

    public void findLoansTest() throws Exception{
        Long id = 1L;

        Loan loan = LoanServiceTest.createLoan();
        loan.setId(id);
        Book book = Book.builder().id(id).isbn("321").build();
        loan.setBook(book);

        BDDMockito.given(loanService.find( Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)) )
                .willReturn( new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10), 1));

        String queryString = String.format("?isbn=%s&customer%s&page=0&size=10",
                book.getIsbn(), loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect( status().isOk() )
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect( jsonPath("totalElements").value(1) )
                .andExpect( jsonPath("pageable.pageSize").value(10) )
                .andExpect( jsonPath("pageable.pageNumber").value(0) );
    }
}
