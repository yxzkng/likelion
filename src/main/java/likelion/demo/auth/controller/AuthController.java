package likelion.demo.auth.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import likelion.demo.auth.dto.request.*;
import likelion.demo.auth.dto.response.*;
import likelion.demo.auth.service.AuthService;
import likelion.demo.global.common.ApiResponse;
import likelion.demo.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> addMember(@Valid @RequestBody SignupRequest signupRequest){
        SignupResponse signupResponse =
                authService.addMember(signupRequest.getLoginId(), signupRequest.getPassword(), signupRequest.getPhoneNumber(), signupRequest.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, "회원 생성에 성공하였습니다.", signupResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest,
                                                            HttpServletRequest httpRequest) {
        LoginResponse loginResponse = authService.login(loginRequest.getLoginId(), loginRequest.getPassword());

        HttpSession session = httpRequest.getSession(true); // 세션이 없으면 새로 생성
        session.setAttribute("LOGIN_MEMBER_ID", loginResponse.getLoginId()); // 로그인 아이디를 세션표에 기록

        return ResponseEntity.ok(ApiResponse.success(200, "로그인 성공", loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {

        // 1. 현재 세션을 가져옵니다.
        // 매개변수 false: 기존 세션이 존재하면 그 세션을 반환하고, 없으면 새로 생성하지 않고 null을 반환합니다.
        HttpSession session = request.getSession(false);

        // 2. 세션이 없거나(로그인 안 됨), 로그인 정보가 없다면 예외 발생
        if (session == null || session.getAttribute("LOGIN_MEMBER_ID") == null) {
            // 401 에러를 발생시키는 예외를 던집니다. (아래 3번 참고)
            throw new UnauthorizedException("로그인이 필요한 서비스입니다.");
        }

        // 3. 세션이 존재하면 완전히 만료(파기)시킵니다.
        session.invalidate();

        // 4. 성공 응답 반환
        return ResponseEntity.ok(ApiResponse.success(200, "로그아웃 성공", null));
    }

}
