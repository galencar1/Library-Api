package com.gfalencar.libraryapi.service;

import com.gfalencar.libraryapi.model.entity.Book;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find( Book filter, Pageable pageRequest );
}
