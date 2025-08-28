package com.example.SechongMaru.oauth;

import com.example.SechongMaru.entity.user.User;
import com.example.SechongMaru.repository.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoLogin implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User kakaoUser = new DefaultOAuth2UserService().loadUser(req);
        Map<String, Object> attrs = kakaoUser.getAttributes();

        // 1) 카카오에서 id, nickname 추출
        Long kakaoId = ((Number) attrs.get("id")).longValue();
        String nickname = extractNickname(attrs);

        // 2) 업서트 (기존 사용자면 업데이트, 없으면 새로 생성)
        User user = userRepository.findById(kakaoId)
                .orElseGet(() -> {
                    User u = new User();
                    u.setId(kakaoId); // PK = 카카오 id
                    return u;
                });
        user.setName(nickname);
        userRepository.save(user);

        // 3) Security 세션에 저장될 OAuth2User 반환
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attrs,
                "id"
        );
    }

    private String extractNickname(Map<String, Object> attrs) {
        try {
            JsonNode root = om.valueToTree(attrs);
            if (root.has("properties") && root.get("properties").has("nickname")) {
                return root.get("properties").get("nickname").asText();
            }
            if (root.has("kakao_account")
                    && root.get("kakao_account").has("profile")
                    && root.get("kakao_account").get("profile").has("nickname")) {
                return root.get("kakao_account").get("profile").get("nickname").asText();
            }
        } catch (Exception ignored) {}
        return "카카오사용자";
    }
}