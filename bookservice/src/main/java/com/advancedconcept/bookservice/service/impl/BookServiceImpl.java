package com.advancedconcept.bookservice.service.impl;

import com.advancedconcept.bookservice.dto.record.BookRequest;
import com.advancedconcept.bookservice.dto.record.BookResponse;
import com.advancedconcept.bookservice.entity.Book;
import com.advancedconcept.bookservice.repository.BookRepository;
import com.advancedconcept.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public void createBook(BookRequest request) {
        Book book = Book.builder()
                .name(request.name())
                .content(request.content())
                .author(request.author())
                .price(request.price())
                .category(request.category())
                .status("INACTIVE")
                .build();
        bookRepository.save(book);
    }

    @Override
    public void updateBookById(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Book not found!"));

        book.setName(request.name());
        book.setContent(request.content());
        book.setAuthor(request.author());
        book.setPrice(request.price());
        book.setCategory(request.category());

        bookRepository.save(book);
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> new BookResponse(
                        book.getId(),
                        book.getName(),
                        book.getContent(),
                        book.getAuthor(),
                        book.getPrice(),
                        book.getCategory(),
                        book.getStatus()
                ))
                .toList();
    }

    @Override
    public void deleteBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found!"));

        bookRepository.delete(book);
    }
}
