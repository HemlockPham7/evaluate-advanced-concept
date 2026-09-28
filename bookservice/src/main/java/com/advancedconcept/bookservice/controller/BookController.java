package com.advancedconcept.bookservice.controller;

import com.advancedconcept.bookservice.dto.record.BookRequest;
import com.advancedconcept.bookservice.dto.record.BookResponse;
import com.advancedconcept.bookservice.service.BookService;
import com.advancedconcept.bookservice.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;
    private final KafkaService kafkaService;

    @PostMapping
    public ResponseEntity<Void> createBook(
            @RequestBody BookRequest request
    ) {
        bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBook(
            @PathVariable Long id,
            @RequestBody BookRequest request
    ) {
        bookService.updateBookById(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id
    ) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/kafka-health")
    public void mqHealthCheck(@RequestBody String message) {
        kafkaService.sendMessage("health-check", message);
    }
}
