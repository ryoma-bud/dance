package com.example.demo.service.userServices;

import com.example.demo.config.jwt.JwtTokenProvider;
import com.example.demo.domian.dto.userDto.*;
import com.example.demo.domian.entity.Role;
import com.example.demo.domian.entity.UserEntity;
import com.example.demo.domian.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // ###################################
    // SignUp
    // ###################################
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {

        // 名前重複チェック
        if (userRepository.existsByName(signUpRequestDto.getName())) {
            throw new IllegalArgumentException("すでに使用されている名前です。");
        }

        // Email重複チェック
        if(userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new IllegalArgumentException("もう登録されているEmailです。");
        }

        // Entityに変換する
        UserEntity userEntity = new UserEntity();
        userEntity.setName(signUpRequestDto.getName());
        userEntity.setEmail(signUpRequestDto.getEmail());

        // frontEndからroleを送らなかったら、基本はUSERに設定
        if (signUpRequestDto.getRole() != null) {
            userEntity.setRole(signUpRequestDto.getRole());
        } else {
            userEntity.setRole(Role.USER);
        }

        // Passwordを暗号化して保存
        String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
        userEntity.setPassword(encodedPassword);

        UserEntity savedUser = userRepository.save(userEntity);

        // Response DTOに変換して Return
        return new SignUpResponseDto(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                "会員登録を完了しました。"
        );
    }

    // ##################################
    // Name重複チェック(会員登録の時使う)
    // ##################################
    public boolean checkNameDuplicate(String name) {
        return userRepository.existsByName(name);
    }

    // ##################################
    // Email重複チェック(会員登録の時使う)
    // ##################################
    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    // ###################################
    // Login
    // ###################################
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        // EmailでUser照会
        UserEntity userEntity = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Emailまたはパスワードが間違っています。"));

        // Password確認(暗号化してないVersion）
        // if(!userEntity.getPassword().equals(loginRequestDto.getPassword())) {
        //     throw new IllegalArgumentException("Emailまたはパスワードが間違っています。");
        // }

        // Passwordを暗号化したらpasswordEncoder.matches()を使う
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), userEntity.getPassword())) {
            throw new IllegalArgumentException("Emailまたはパスワードが間違っています。");
        }

        // JwtTokenProviderを使用してToken生成
        String token = jwtTokenProvider.createToken(userEntity.getEmail(), userEntity.getRole().name());

        UserInfoDto userInfoDto = new UserInfoDto(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getRole().name()
        );

        return new LoginResponseDto(token, userInfoDto);
    }

}
