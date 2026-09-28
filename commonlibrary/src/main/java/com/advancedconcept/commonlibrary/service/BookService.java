package com.advancedconcept.commonlibrary.service;

import com.advancedconcept.commonlibrary.dto.record.BookRequest;
import com.advancedconcept.commonlibrary.dto.record.BookResponse;

import java.util.List;

public interface BookService {

    void createBook(BookRequest request);

    void updateBookById(Long id, BookRequest request);

    List<BookResponse> getAllBooks();

    void deleteBookById(Long id);
}