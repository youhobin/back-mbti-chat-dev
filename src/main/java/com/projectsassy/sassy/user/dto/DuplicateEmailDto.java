package com.projectsassy.sassy.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class DuplicateEmailDto {

    @NotNull(message = "이메일은 Null 일 수 없습니다.")
    private String email;

    @Builder
    private DuplicateEmailDto(String email) {
        this.email = email;
    }
}
