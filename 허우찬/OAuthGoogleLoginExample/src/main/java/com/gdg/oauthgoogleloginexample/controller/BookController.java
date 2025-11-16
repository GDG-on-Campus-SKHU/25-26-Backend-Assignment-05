package com.gdg.oauthgoogleloginexample.controller;

import com.gdg.oauthgoogleloginexample.dto.BookDto;
import com.gdg.oauthgoogleloginexample.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDto> createBook(@AuthenticationPrincipal String userId, @RequestBody BookDto requestDto) {
        BookDto responseDto = bookService.createBook(Long.parseLong(userId), requestDto);
        return ResponseEntity.created(URI.create("/api/books/" + responseDto.getId()))
                .body(responseDto);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long bookId) {
        BookDto bookDto = bookService.findBookById(bookId);
        return ResponseEntity.ok(bookDto);
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<BookDto> books = bookService.findAllBooks();
        return ResponseEntity.ok(books);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookDto> updateBook(@AuthenticationPrincipal String userId, @PathVariable Long bookId, @RequestBody BookDto requestDto) {
        BookDto updatedBook = bookService.updateBook(Long.parseLong(userId), bookId, requestDto);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@AuthenticationPrincipal String userId, @PathVariable Long bookId) {
        bookService.deleteBook(Long.parseLong(userId), bookId);
        return ResponseEntity.noContent().build();
    }
}
