// src/main/java/com/example/SechongMaru/my/MyController.java
package com.example.SechongMaru.my;

import com.example.SechongMaru.dto.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MyController {

    private final MyService myService;

    /** Jackson 바인딩용 페이로드 (public static) */
    public static record MyUpdatePayload(
            UserUpdateProfileRequestDto profile,
            InterestsUpdateRequestDto interests
    ) {}

    public static record MyInfoPayload(
            UserCreateRequestDto user,
            InterestsUpdateRequestDto interests
    ) {}

    /** 마이페이지 진입(조회) */
    @GetMapping(value = "/api/my", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDto getMy(@AuthenticationPrincipal OAuth2User principal) {
        Long userId = requireLogin(principal);
        return myService.getMy(userId);
    }

    /** 마이페이지 정보수정: 프로필 + 관심사 (PUT) */
    @PutMapping(value = "/api/my",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDto updateMy(@RequestBody(required = false) MyUpdatePayload body,
                                    @AuthenticationPrincipal OAuth2User principal) {
        Long userId = requireLogin(principal);
        return myService.updateMy(
                userId,
                body != null ? body.profile() : null,
                body != null ? body.interests() : null
        );
    }

    /** 사용자 기본정보입력: 이름/생일/기본정보 + 관심사 (POST/PUT 둘 다 허용) */
    @RequestMapping(
            value = {"/api/myinfo", "/api/myinfo/"},
            method = {RequestMethod.POST, RequestMethod.PUT},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponseDto upsertMyInfo(@RequestBody(required = false) MyInfoPayload body,
                                        @AuthenticationPrincipal OAuth2User principal) {
        Long userId = requireLogin(principal);
        return myService.upsertMyInfo(
                userId,
                body != null ? body.user() : null,
                body != null ? body.interests() : null
        );
    }

    /** Security Principal에서 카카오 id 추출 (KakaoLogin에서 name attr key = "id") */
    private Long requireLogin(OAuth2User principal) {
        if (principal == null) throw new UnauthorizedException("로그인이 필요합니다.");
        Object id = principal.getAttribute("id");
        if (id instanceof Number n) return n.longValue();
        if (id instanceof String s && !s.isBlank()) return Long.parseLong(s);
        throw new UnauthorizedException("카카오 사용자 id를 찾을 수 없습니다.");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }
}
