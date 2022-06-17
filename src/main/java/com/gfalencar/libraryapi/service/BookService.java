package com.gfalencar.libraryapi.service;

import com.gfalencar.libraryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);
}
