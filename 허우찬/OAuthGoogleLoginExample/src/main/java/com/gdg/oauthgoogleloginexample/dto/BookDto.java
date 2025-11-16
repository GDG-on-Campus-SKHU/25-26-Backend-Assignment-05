package com.gdg.oauthgoogleloginexample.dto;

import com.gdg.oauthgoogleloginexample.domain.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookDto {
    private Long id;
    private String title;
    private String publisher;

    public static BookDto from(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setPublisher(book.getPublisher());
        return dto;
    }
}
