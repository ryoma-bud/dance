package com.example.demo.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    // Tokenを作って、検証する機能を持っているJwtTokenProvider！
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // Userが送ったRequestのHeaderからJwtTokenを取り出す
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        System.out.println("Tokenを見せてください。" + token);

        if (token != null) {
            if (jwtTokenProvider.validateToken(token)) {

                // 取り出したTokenに問題がなければTokenからUserの情報をもとに”認証Object”生成
                // ”Authentication”はこの人は認証された人だという証明書と同一
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                // SpringのSecurityの倉庫（SecurityContextHolder）にこの証明書を入れとく
                // このように入れとくと、Requestが終わるまでにServerは”このUserは認証されてる”状態で認識
                // Profile編集のような新しいRequestができる時ごと、SecurityContextが新しくできる
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("認証成功：　" + authentication.getName());
            } else {
                System.out.println("認証失敗　OR　Tokenがない");
            }
        }

        // 次のStepへ
        // このCodeがないとRequestがここと止まるので、必ず書かないといけない
        chain.doFilter(request, response);

    }
}
