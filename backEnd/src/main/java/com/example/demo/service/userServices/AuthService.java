package com.example.demo.service.userServices;

import com.example.demo.config.jwt.JwtTokenProvider;
import com.example.demo.domian.dto.userDto.*;
import com.example.demo.domian.entity.Role;
import com.example.demo.domian.entity.UserEntity;
import com.example.demo.domian.repository.UserRepository;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // ###################################
    // SignUp
    // ###################################
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto, EmailService emailService) {

        // Email重複チェック
        if(userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new IllegalArgumentException("もう登録されているEmailです。");
        }

        // Email認証成功状態確認
        boolean isVerified = emailService.isEmailVerified(signUpRequestDto.getEmail());
        if (!isVerified) {
            throw new IllegalArgumentException("Emailの認証が完了していません。");
        }

        // 名前重複チェック
        if (userRepository.existsByName(signUpRequestDto.getName())) {
            throw new IllegalArgumentException("すでに使用されている名前です。");
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

        // 最終的に会員登録を完了したら完了FlagはRedisから削除
        emailService.deleteVerifiedStatus(signUpRequestDto.getEmail());

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

    // ####################################
    // Password 再設定
    // ####################################
    @Transactional
    public void updatePasswordWithVerification(String email, String newPassword, EmailService emailService) {

        // 加入しているUSERなのか確認
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("登録されていないEmailです。"));

        // 入力した認証コードが正しいか確認
        boolean isValid = emailService.isEmailVerified(email);
        if (!isValid) {
            throw new IllegalArgumentException("認証番号が一致しないか、有効期限が切れました。");
        }

        // 新しいPasswordを暗号化後、保存
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);

        // 完了FlagをRedisから削除
        emailService.deleteVerifiedStatus(email);

    }

}
