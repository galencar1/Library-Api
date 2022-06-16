package com.gfalencar.libraryapi.model.repository;

import com.gfalencar.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
