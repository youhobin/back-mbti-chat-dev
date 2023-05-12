package com.projectsassy.sassy.user.repository;

import com.projectsassy.sassy.user.domain.Email;
import com.projectsassy.sassy.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("로그인 ID로 유저를 조회한다.")
    @Test
    void findUserOptionalByLoginId() {
        // given
        String loginId = "ghdb132";
        User user1 = createUser(loginId, "12345", "ghdb132@naver.com", "image", "ISFP", "hobin", "male");
        User user2 = createUser("asdf", "12345", "asdf@naver.com", "image", "ENFP", "hihi", "male");
        User user3 = createUser("qwg45", "12345", "qwe35@naver.com", "image", "ENTP", "bick", "female");
        userRepository.saveAll(List.of(user1, user2, user3));

        // when
        User findUser = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException();
                });

        //then
        assertThat(findUser)
                .extracting("loginId", "password", "nickname", "gender",
                        "mbti", "image", "point")
                .containsExactly(loginId, "12345", "hobin", "male", "ISFP", "image", 0);
    }

    @DisplayName("로그인 ID로 유저 조회 시 등록된 ID 값이 없다.")
    @Test
    void failFindUserByLoginId() {
        // given
        String loginId = "ghdb132";
        User user1 = createUser(loginId, "12345", "ghdb132@naver.com", "image", "ISFP", "hobin", "male");
        User user2 = createUser("asdf", "12345", "asdf@naver.com", "image", "ENFP", "hihi", "male");
        User user3 = createUser("qwg45", "12345", "qwe35@naver.com", "image", "ENTP", "bick", "female");
        userRepository.saveAll(List.of(user1, user2, user3));

        // when
        Optional<User> findUser = userRepository.findByLoginId("NotLoginId");

        // then
        assertThat(findUser).isEmpty();
    }

    @DisplayName("이메일로 유저를 조회한다.")
    @Test
    void findUserByEmail() {
        // given
        String email = "ghdb132@naver.com";
        User user1 = createUser("ghdb132", "12345", email, "image", "ISFP", "hobin", "male");
        User user2 = createUser("asdf", "12345", "asdf@naver.com", "image", "ENFP", "hihi", "male");
        User user3 = createUser("qwg45", "12345", "qwe35@naver.com", "image", "ENTP", "bick", "female");
        userRepository.saveAll(List.of(user1, user2, user3));

        // when
        User findUser = userRepository.findByEmail(new Email(email))
                .orElseThrow(() -> {
                    throw new IllegalArgumentException();
                });

        //then
        assertThat(findUser)
                .extracting("loginId", "password", "email.email", "image", "mbti", "nickname", "gender", "point")
                .containsExactly("ghdb132", "12345", email, "image", "ISFP", "hobin", "male", 0);
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
