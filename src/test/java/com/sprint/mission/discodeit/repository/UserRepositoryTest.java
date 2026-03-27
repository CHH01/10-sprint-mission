package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.sprint.mission.discodeit.DiscodeitApplication;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = DiscodeitApplication.class))
@ActiveProfiles("test")
@EnableJpaAuditing
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이름으로 유저 존재 여부 확인 성공")
    void existsByName_Success() {
        // given
        User user = new User("testUser", "test@email.com", "password", null);
        entityManager.persist(user);
        entityManager.flush();

        // when
        boolean result = userRepository.existsByName("testUser");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("이름으로 유저 존재 여부 확인 실패 - 존재하지 않는 이름")
    void existsByName_Fail() {
        // when
        boolean result = userRepository.existsByName("nonExistentUser");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("이메일로 유저 존재 여부 확인 성공")
    void existsByEmail_Success() {
        // given
        User user = new User("testUser", "test@email.com", "password", null);
        entityManager.persist(user);
        entityManager.flush();

        // when
        boolean result = userRepository.existsByEmail("test@email.com");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("이메일로 유저 존재 여부 확인 실패 - 존재하지 않는 이메일")
    void existsByEmail_Fail() {
        // when
        boolean result = userRepository.existsByEmail("nonexistent@email.com");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("이름으로 유저 찾기 성공")
    void findByName_Success() {
        // given
        User user = new User("findMe", "find@email.com", "password", null);
        entityManager.persist(user);
        entityManager.flush();

        // when
        User foundUser = userRepository.findByName("findMe").orElse(null);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("findMe");
    }

    @Test
    @DisplayName("이름으로 유저 찾기 실패 - 존재하지 않는 이름")
    void findByName_Fail() {
        // when
        User foundUser = userRepository.findByName("nonExistentUser").orElse(null);

        // then
        assertThat(foundUser).isNull();
    }
}
