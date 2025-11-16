package com.skhu.oauthgoogleloginpr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //JPA Auditing 기능 활성화
public class OAuthGoogleLoginPrApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuthGoogleLoginPrApplication.class, args);
    }

}
