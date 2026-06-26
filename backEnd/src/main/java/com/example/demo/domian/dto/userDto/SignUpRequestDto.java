package com.example.demo.domian.dto.userDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

// 会員登録時、入力してもらう項目
@Setter
@Getter
public class SignUpRequestDto {

    @NotBlank(message = "名前は必須です。")
    @Pattern(regexp = "^(?=.*[a-zA-Z가-힣])[a-zA-Z가-힣0-9]+$", message = "名前は文字が必須であり、数字は使用可能ですが、特殊文字は使用できません。")
    private String name;

    @NotBlank(message = "Emailは必須です。")
    @Email(message = "正しいEmailの型式ではありません。")
    private String email;

    @NotBlank(message = "パスワードは必須です。")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "パスワードは英語1文字以上、数字1文字以上、全部で8文字以上にする必要があります。")
    private String password;

}
