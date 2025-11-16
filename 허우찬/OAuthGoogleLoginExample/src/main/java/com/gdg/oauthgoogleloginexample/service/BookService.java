package com.gdg.oauthgoogleloginexample.service;

import com.gdg.oauthgoogleloginexample.domain.Book;
import com.gdg.oauthgoogleloginexample.domain.User;
import com.gdg.oauthgoogleloginexample.dto.BookDto;
import com.gdg.oauthgoogleloginexample.repository.BookRepository;
import com.gdg.oauthgoogleloginexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookDto createBook(Long userId, BookDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id=" + userId));

        Book book = Book.builder()
                .title(requestDto.getTitle())
                .publisher(requestDto.getPublisher())
                .registeredBy(user)
                .build();

        Book savedBook = bookRepository.save(book);
        return BookDto.from(savedBook);
    }

    public BookDto findBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 책을 찾을 수 없습니다. id=" + bookId));
        return BookDto.from(book);
    }

    public List<BookDto> findAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDto updateBook(Long userId, Long bookId, BookDto requestDto) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 책을 찾을 수 없습니다. id=" + bookId));

        if (!Objects.equals(book.getRegisteredBy().getId(), userId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        book.update(requestDto.getTitle(), requestDto.getPublisher());
        return BookDto.from(book);
    }

    @Transactional
    public void deleteBook(Long userId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 책을 찾을 수 없습니다. id=" + bookId));

        if (!Objects.equals(book.getRegisteredBy().getId(), userId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        bookRepository.delete(book);
    }
}
