package likelion.demo.auth.controller;

import jakarta.servlet.http.HttpSession;
import likelion.demo.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // TODO: auth 팀이 회원가입, 로그인, 로그아웃 구현 예정

    @PostMapping("/test-login")
    public ResponseEntity<ApiResponse<String>> testLogin(HttpSession session) {
        session.setAttribute("memberId", 1L);
        return ResponseEntity.ok(ApiResponse.success(200, "테스트 로그인 성공", "memberId=1 세션 저장됨"));
    }
}
