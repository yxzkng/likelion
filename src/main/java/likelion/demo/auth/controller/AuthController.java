package likelion.demo.auth.controller;

import jakarta.servlet.http.HttpSession;
import likelion.demo.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 임시 로그인 컨트롤러 (테스트용)
 * - auth 팀이 실제 구현으로 교체할 예정
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/test-login")
    public ResponseEntity<ApiResponse<String>> testLogin(HttpSession session) {
        session.setAttribute("memberId", 1L);
        return ResponseEntity.ok(ApiResponse.success(200, "테스트 로그인 성공", "memberId=1 세션 저장됨"));
    }
}
