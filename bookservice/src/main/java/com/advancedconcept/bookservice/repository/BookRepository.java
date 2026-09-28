package com.advancedconcept.bookservice.repository;

import com.advancedconcept.bookservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
