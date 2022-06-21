package com.gfalencar.libraryapi.service.impl;

import com.gfalencar.libraryapi.api.dto.LoanFilterDTO;
import com.gfalencar.libraryapi.exception.BusinessException;
import com.gfalencar.libraryapi.model.entity.Loan;
import com.gfalencar.libraryapi.model.repository.LoanRepository;
import com.gfalencar.libraryapi.service.LoanService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class LoanServiceImpl implements LoanService {
    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if(repository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return this.repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageRequest) {
//        Example<Loan> example = Example.of(filterDTO,
//                ExampleMatcher
//                        .matching()
//                        .withIgnoreCase()
//                        .withIgnoreNullValues()
//                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) );
//
//        return repository.findAll(example, pageRequest);
        return null;
   }
}
