package com.projectsassy.sassy.user.service;

import com.projectsassy.sassy.common.exception.user.DuplicatedException;
import com.projectsassy.sassy.common.util.RedisUtil;
import com.projectsassy.sassy.token.TokenProvider;
import com.projectsassy.sassy.user.domain.User;
import com.projectsassy.sassy.user.dto.*;
import com.projectsassy.sassy.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private TokenProvider tokenProvider;

    @MockBean
    private RedisUtil redisUtil;

    @DisplayName("새로운 유저 등록. 비밀번호는 인코딩된 값으로 등록된다.")
    @Test
    void createUser() {
        // given
        UserJoinDto userJoinDto = UserJoinDto.builder()
                .loginId("ghdb132")
                .password("12345")
                .nickname("hobin")
                .email("ghdb132@naver.com")
                .gender("male")
                .mbti("ISFP")
                .build();

        // when
        userService.join(userJoinDto);
        User findUser = userRepository.findByLoginId("ghdb132")
                .orElseThrow(() -> {
                    throw new IllegalArgumentException();
                });

        //then
        assertThat(findUser)
                .extracting("loginId", "nickname", "email.email", "gender", "mbti")
                .containsExactly("ghdb132", "hobin", "ghdb132@naver.com", "male", "ISFP");

        assertThat(findUser.getPassword()).isNotEqualTo("12345");
    }

    @DisplayName("등록된 유저 중 로그인 ID 중복이 있는지 검사한다.")
    @Test
    void duplicateLoginId() {
        // given
        User user = createUser("ghdb132", "12345", "ghdb132@naver.com", "image", "ISFP", "hobin", "male");
        userRepository.save(user);

        DuplicateLoginIdDto duplicateLoginIdDto = DuplicateLoginIdDto.builder()
                .loginId("ghdb132")
                .build();

        // when // then
        assertThatThrownBy(() -> userService.duplicateLoginId(duplicateLoginIdDto))
                .isInstanceOf(DuplicatedException.class)
                .hasMessage("중복된 아이디입니다.");

    }

    @DisplayName("등록된 유저 중 이메일에 중복이 있는지 검사한다.")
    @Test
    void duplicateEmail() {
        // given
        User user = createUser("ghdb132", "12345", "ghdb132@naver.com", "image", "ISFP", "hobin", "male");
        userRepository.save(user);

        DuplicateEmailDto duplicateEmailDto = DuplicateEmailDto.builder()
                .email("ghdb132@naver.com")
                .build();

        // when // then
        assertThatThrownBy(() -> userService.duplicateEmail(duplicateEmailDto))
                .isInstanceOf(DuplicatedException.class)
                .hasMessage("중복된 이메일입니다.");
    }

    @DisplayName("등록된 아이디와 비밀번호로 로그인을 진행한다.")
    @Test
    void login() {
        // given
        UserJoinDto userJoinDto = UserJoinDto.builder()
                .loginId("ghdb132")
                .password("12345")
                .nickname("hobin")
                .email("ghdb132@naver.com")
                .gender("male")
                .mbti("ISFP")
                .build();

        userService.join(userJoinDto);

        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("ghdb132")
                .password("12345")
                .build();

        doNothing().when(redisUtil).setDataExpire(anyString(), anyString(), anyLong());

        // when
        LoginResponse response = userService.login(loginRequest);

        //then
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(response.getAccessTokenExpiresIn()).isNotNull();
        assertThat(response.getNickname()).isEqualTo("hobin");

    }

    private User createUser(String loginId, String password, String email, String image, String mbti, String nickname, String gender) {
        return User.builder()
                .loginId(loginId)
                .password(password)
                .email(email)
                .image(image)
                .mbti(mbti)
                .nickname(nickname)
                .gender(gender)
                .build();
    }
}