package com.example.demo.service.userServices;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// 本人確認用Email送信・Redis保存
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String AUTH_CODE_PREFIX = "AuthCode";
    private static final long AUTH_CODE_EXPIRATION = 5 * 60;    // 5分

    // ###########################################
    // 認証番号生成・メール発送
    // ###########################################
    public boolean sendAuthCode(String toEmail) {

        boolean isResend = Boolean.TRUE.equals(stringRedisTemplate.hasKey(AUTH_CODE_PREFIX + toEmail));

        // 6桁乱数生成
        String authCode = createAuthCode();

        // メールの内容作成
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(toEmail);

        simpleMailMessage.setFrom("zergling2565@gmail.com");    //
        simpleMailMessage.setSubject("DanStar 会員登録Email認証番号");
        simpleMailMessage.setText("認証番号は [" + authCode + "] です。\n5分以内に入力してください。");

        javaMailSender.send(simpleMailMessage);

        // Redisに保存（Key：”AuthCode：Email”、Value：”認証番号”、満了時間：5分）
        stringRedisTemplate.opsForValue().set(
                AUTH_CODE_PREFIX + toEmail,
                authCode,
                AUTH_CODE_EXPIRATION,
                TimeUnit.SECONDS
        );

        // 再送信なのかをReturn
        return isResend;
    }

    // ######################################
    // 認証番号検証
    // ######################################
    public boolean verifyAuthCode(String email, String inputCode) {
        String savedCode = stringRedisTemplate.opsForValue().get(AUTH_CODE_PREFIX + email);

        if (savedCode != null && savedCode.equals(inputCode)) {
            // 認証成功し、Redisでは削除（再使用防止）
            stringRedisTemplate.delete(AUTH_CODE_PREFIX + email);
            return true;
        }
        return false;
    }

    // 6桁乱数生成
    private String createAuthCode() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            key.append(secureRandom.nextInt(10));
        }
        return key.toString();
    }
}
