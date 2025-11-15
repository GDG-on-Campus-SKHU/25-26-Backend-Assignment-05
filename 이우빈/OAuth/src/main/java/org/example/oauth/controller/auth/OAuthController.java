package org.example.oauth.controller.auth;

import lombok.RequiredArgsConstructor;
import org.example.oauth.common.Constants;
import org.example.oauth.dto.TokenDto;
import org.example.oauth.service.auth.GoogleOAuthService;
import org.example.oauth.util.CookieUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class OAuthController {

    private final GoogleOAuthService googleOAuthService;

    private static final String DEFAULT_VALUE = "state";

    @GetMapping("/authorize/google")
    public String redirectToGoogle(@RequestParam(defaultValue = DEFAULT_VALUE) String state) {
        String url = googleOAuthService.buildAuthorizationUrl(state);
        return "redirect:" + url;
    }

    @GetMapping("/callback/google")
    public ResponseEntity<Void> googleCallback(@RequestParam("code") String code) {
        TokenDto tokens = googleOAuthService.loginWithCode(code);

        return ResponseEntity.status(302)
                .headers(httpHeaders -> {
                    CookieUtil.rotateRefreshCookies(httpHeaders, tokens.getRefreshToken(), Constants.TWO_WEEKS);
                    httpHeaders.add(HttpHeaders.LOCATION, Constants.HEADER_VALUE);
                })
                .build();
    }

    @GetMapping("/authorize/google/url")
    @ResponseBody
    public String googleAuthUrl(@RequestParam(defaultValue = DEFAULT_VALUE) String state) {
        return googleOAuthService.buildAuthorizationUrl(state);
    }
}
