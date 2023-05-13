package com.projectsassy.sassy.user.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {

    @NotNull(message = "로그인 아이디는 Null 일 수 없습니다.")
    private String loginId;

    @NotNull(message = "비밀번호는 Null 일 수 없습니다.")
    private String password;

    @Builder
    private LoginRequest(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
