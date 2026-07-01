package com.example.demo.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

// Tokenを生成
// Tokenが本物なのか偽物なのか検証
// TokenからUserの情報（Email）を取り出す
@Component
public class JwtTokenProvider {

    // Propertiesから持ってくる
    @Value("${jwt.secret.key}")
    private String secretKey;

    // Tokenの有効期限（1時間）
    @Value("${jwt.expiration}")
    private long tokenValidityInMilliseconds;

    private Key key;    // 実際の暗号化に使用するKey

    // Springがこのクラスを生成した後、自動二一回だけ実行されるMethod
    // 設定したsecretKeyをパソコンが使いやすくEncodingし、Settingする過程
    @PostConstruct
    protected void init() {

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Token生成＿ログイン成功した際に呼び出される
    // @Param　email　Userを区別できる値

    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("auth", "ROLE_" + role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)  // 内容物入れる
                .setIssuedAt(now)   // 作った時間
                .setExpiration(validity)    // 満了時間
                .signWith(key, SignatureAlgorithm.HS256)    //  Serverのスタンプで署名押印
                .compact();     //  Stringで変換して完成
    }

    // Tokenが有効なやつやったら、ログイン成功状態にしてくれるObjectを生成
    public Authentication getAuthentication(String token) {

        // Tokenを読み込んで内部のデータを全部取得
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // TokenからEmail（Subject）と権限情報（auth）を取得
        String email = claims.getSubject();
        String role = claims.get("auth", String.class);

        //  取得した権限文字例をSecurityが理解してもらえるObject（GrantedAuthority）に変換
        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(role));

        // Emailと一緒に実際の権限リストを盛り込んでObjectをReturn
        return new UsernamePasswordAuthenticationToken(email, "", authorities);
    }

    // Tokenを取り出す＿FrontEndが送ったRequestからTokenだけ取り出す
    public String resolveToken(HttpServletRequest httpServletRequest) {
        // FrontEndは慣例的に"Authorization: Bearer [Tokenの値]" のように送るから
        String bearerToken = httpServletRequest.getHeader("Authorization");

        // ”Bearerの後ろにある実際のTokenだけ切り取る”
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);    // ”Bearer ”が7文字なので、その後からSubStringでとってくる
        }
        return null;
    }

    // Token検査＿Tokenが本物なのか時間が過ぎたかを確認
    public boolean validateToken(String token) {
        try {
            // Serverが持ってるkeyでTokenを解く
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            // 満了時間が今よりbeforeでしたらTrue
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            // 本物のTokenじゃなかったり、満了されてたらError発生
            return false;
        }
    }
}
