package com.advancedconcept.bookservice.service;

import com.advancedconcept.bookservice.dto.record.BookRequest;
import com.advancedconcept.bookservice.dto.record.BookResponse;

import java.util.List;

public interface BookService {

    void createBook(BookRequest request);

    void updateBookById(Long id, BookRequest request);

    List<BookResponse> getAllBooks();

    void deleteBookById(Long id);
}
