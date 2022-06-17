package com.gfalencar.libraryapi.service.impl;

import com.gfalencar.libraryapi.exception.BusinessException;
import com.gfalencar.libraryapi.model.entity.Book;
import com.gfalencar.libraryapi.repository.BookRepository;
import com.gfalencar.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("ISBN já cadastrado!");
        }

        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return Optional.empty();
    }
}
