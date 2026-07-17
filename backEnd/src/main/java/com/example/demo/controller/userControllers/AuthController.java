package com.example.demo.controller.userControllers;

import com.example.demo.domain.dto.userDto.LoginRequestDto;
import com.example.demo.domain.dto.userDto.LoginResponseDto;
import com.example.demo.domain.dto.userDto.SignUpRequestDto;
import com.example.demo.domain.dto.userDto.SignUpResponseDto;
import com.example.demo.service.userServices.AuthService;
import com.example.demo.service.userServices.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    // ###################################
    // 会員登録時の認証番号発送要請
    // ###################################
    @PostMapping("/email/send")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // Email重複チェック
        if (authService.checkEmailDuplicate(email)) {
            // もうDBに保存されてるEmailでしたら、400エラーメッセージ変換
            return ResponseEntity.badRequest().body(Map.of("message", "すでに登録されているEmailです。"));
        }

        // Emailに認証はメール送る
        boolean isResend = emailService.sendAuthCode(email);

        // 再送信によってメッセージを設定する
        String message = isResend
                ? "認証番号を再送信しました。メールをご確認ください。"
                : "認証番号を送信しました。メールをご確認ください。";


        return ResponseEntity.ok(Map.of("message", message));
    }

    // ###################################
    // 認証番号確認要請
    // ###################################
    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean isVerified = emailService.verifyAuthCode(email, code);

        if (isVerified) {
            return ResponseEntity.ok(Map.of("message", "Email確認が完了されました。"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "確認番号が間違ってたか、満了されています。"));
        }
    }

    // ###################################
    // SignUp
    // ###################################
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {

        try {
            SignUpResponseDto signUpResponseDto = authService.signUp(signUpRequestDto, emailService);
            return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ##########################
    // パスワード再設定用の認証番号発送
    // ##########################
    @PostMapping("/password/email/send")
    public ResponseEntity<?> sendResetPasswordEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Emailを入力してください。"));
        }

        // Email存在Check（会員じゃない場合、発送拒否）
        if (!authService.checkEmailDuplicate(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "登録されていないEmailです。"));
        }

        // Emailへ認証番号発送
        boolean isResend = emailService.sendAuthCode(email);

        String message = isResend
                ? "認証番号を再送信しました。メールをご確認ください。"
                : "認証番号を送信しました。メールをご確認ください。";

        return ResponseEntity.ok(Map.of("message", message));
    }


    // ####################################
    // Password再設定
    // ####################################
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Emailを入力してください。"));
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "新しいパスワードを入力してください。"));
        }

        try{
            authService.updatePasswordWithVerification(email, newPassword, emailService);

            return ResponseEntity.ok(Map.of("message", "パスワードの再設定が完了しました。新しいパスワードでログインしてください。"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "パスワード再設定中にエラーが発生しました。"));
        }
    }

    // ####################################
    // Name重複チェック
    // ####################################
    @GetMapping("/check-name")
    public ResponseEntity<?> checkName(@RequestParam String name) {
        boolean isDuplicate = authService.checkNameDuplicate(name);

        if (isDuplicate) {
            return ResponseEntity.badRequest().body(Map.of("message", "すでに使用されているNameです。"));
        } else {
            return ResponseEntity.ok(Map.of("message", "使用可能なNameです。"));
        }
    }

    // ###################################
    // Login
    // ###################################
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
            return ResponseEntity.ok(loginResponseDto);
        } catch (IllegalArgumentException e) {
            // パスワードがなかったり、Userがない場合例外処理
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

}
