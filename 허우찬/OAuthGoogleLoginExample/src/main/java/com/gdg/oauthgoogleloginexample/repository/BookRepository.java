package com.gdg.oauthgoogleloginexample.repository;

import com.gdg.oauthgoogleloginexample.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
