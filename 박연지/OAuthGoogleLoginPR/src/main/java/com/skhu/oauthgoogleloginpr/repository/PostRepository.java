package com.skhu.oauthgoogleloginpr.repository;
import com.skhu.oauthgoogleloginpr.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
