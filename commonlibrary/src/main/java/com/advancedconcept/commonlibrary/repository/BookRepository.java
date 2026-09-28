package com.advancedconcept.commonlibrary.repository;

import com.advancedconcept.commonlibrary.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}