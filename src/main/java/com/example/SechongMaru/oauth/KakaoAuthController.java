package com.example.SechongMaru.oauth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class KakaoAuthController {

    private final OAuth2AuthorizedClientService clientService;
    private final RestClient http = RestClient.create();

    // 카카오 동의 화면으로
    @GetMapping("/authorize")
    public ResponseEntity<Void> authorize() {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create("/oauth2/authorization/kakao"));
        return new ResponseEntity<>(h, HttpStatus.FOUND);
    }

    // 카카오 로그아웃(토큰 무효화) + 우리 세션 종료
    @GetMapping("/logout")
    public ResponseEntity<?> logout(OAuth2AuthenticationToken auth, HttpServletRequest req) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "unauthenticated"));

        OAuth2AuthorizedClient client =
                clientService.loadAuthorizedClient(auth.getAuthorizedClientRegistrationId(), auth.getName());
        String accessToken = client.getAccessToken().getTokenValue();

        Map<?, ?> kakao = http.post()
                .uri("https://kapi.kakao.com/v1/user/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);

        req.getSession().invalidate();
        return ResponseEntity.ok(kakao);
    }

    // 카카오 연결 해제 + 우리 세션 종료
    @GetMapping("/unlink")
    public ResponseEntity<?> unlink(OAuth2AuthenticationToken auth, HttpServletRequest req) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "unauthenticated"));

        OAuth2AuthorizedClient client =
                clientService.loadAuthorizedClient(auth.getAuthorizedClientRegistrationId(), auth.getName());
        String accessToken = client.getAccessToken().getTokenValue();

        Map<?, ?> kakao = http.post()
                .uri("https://kapi.kakao.com/v1/user/unlink")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);

        req.getSession().invalidate();
        return ResponseEntity.ok(kakao);
    }
}
