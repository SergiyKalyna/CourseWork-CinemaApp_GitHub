package com.geekhub.user;

import com.geekhub.exception.UserNotFoundException;
import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {UserRepository.class, UserMapper.class})
@Sql(scripts = "classpath:schema.sql")
class UserRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users");
    }

    @Test
    void no_history_records_in_db() {
        long userCount = userRepository.getAll().size();
        assertThat(userCount).isZero();
    }

    @Test
    void nothing_happened_when_trying_to_delete_not_existing_user() {
        assertThatCode(() -> userRepository.delete(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DirtiesContext
    void check_delete_user_method() {
        assertThat(userRepository.getAll().size()).isEqualTo(0);

        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        userRepository.saveUser(user);
        assertThat(userRepository.getAll().size()).isEqualTo(1);

        userRepository.delete(2L);
        assertThat(userRepository.getAll().size()).isEqualTo(0);
    }

    @Test
    void if_user_not_found_should_throw_exception() {
        assertThrows(UserNotFoundException.class, () -> userRepository.findById(1L));
    }

    @Test
    @DirtiesContext
    void check_return_user_method() {
        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        userRepository.saveUser(user);

        User userFromDb = userRepository.findById(2L);

        assertThat(userFromDb).extracting(User::getId).isEqualTo(user.getId());
        assertThat(userFromDb).extracting(User::getLogin).isEqualTo(user.getLogin());
    }

    @Test
    @DirtiesContext
    void check_multiply_add_user() {
        assertThat(userRepository.getAll().size()).isEqualTo(0);
        User user1 = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        User user2 = new User(3L, "user name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        userRepository.saveUser(user1);
        userRepository.saveUser(user2);

        assertThat(userRepository.getAll().size()).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    void check_right_find_by_id_method() {
        String searchLogin = "name";
        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        userRepository.saveUser(user);

        User userFromDb = userRepository.findByLogin(searchLogin);

        assertThat(userFromDb).extracting(User::getId).isEqualTo(user.getId());
        assertThat(userFromDb).extracting(User::getLogin).isEqualTo(user.getLogin());
    }

    @Test
    @DirtiesContext
    void check_right_work_update_method() {
        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        userRepository.saveUser(user);

        String newFirstName = "After update name";
        user.setFirstName(newFirstName);

        userRepository.updateUser(2L, user);

        User userFromDb = userRepository.findById(2L);

        assertThat(userFromDb).extracting(User::getFirstName).isEqualTo(newFirstName);
        assertThat(userFromDb).extracting(User::getId).isEqualTo(user.getId());
        assertThat(userFromDb).extracting(User::getLogin).isEqualTo(user.getLogin());
    }

    @Test
    @DirtiesContext
    void password_should_be_changed() {
        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        String newPassword = "new password";

        userRepository.saveUser(user);

        userRepository.changePassword(2L, newPassword);

        User userFromDb = userRepository.findById(2L);

        assertThat(userFromDb).extracting(User::getPassword).isEqualTo(newPassword);
    }
}
