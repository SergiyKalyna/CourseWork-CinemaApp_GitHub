package com.geekhub.user;

import com.geekhub.exception.UserAuthenticationException;
import com.geekhub.exception.UserNotFoundException;
import com.geekhub.exception.ValidationException;
import com.geekhub.exception.WrongPasswordException;
import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Autowired
    BindingResult bindingResult;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void findById_check_repository_call() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(user);

        userService.findById(1L);

        verify(userRepository).findById(1L);
    }

    @Test
    void findById_if_user_not_found() {
        when(userRepository.findById(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void findById_check_return_result() {
        User expected = new User(1L, "login", "pass", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(expected);

        User actual = userService.findById(1L);

        assertEquals(expected, actual);
    }

    @Test
    void delete_check_call_checkedMethod() {
        User expected = new User(1L, "login", "pass", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(expected);

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
    }

    @Test
    void if_user_toDelete_was_not_found() {
        when(userRepository.findById(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void check_call_repository_method_toDelete() {
        User expected = new User(1L, "login", "pass", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(expected);

        userService.deleteUser(1L);

        verify(userRepository).delete(1L);
    }

    @Test
    void success_delete_user() {
        User expected = new User(1L, "login", "pass", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(expected);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
    }

    @Test
    void getAllUsers_check_call_repository_method() {
        List<User> users = new ArrayList<>();
        when(userRepository.getAll()).thenReturn(users);

        userService.getAllUsers();

        verify(userRepository).getAll();
    }

    @Test
    void getAllUsers_check_result() {
        User user = new User(1L, "login", "pass", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        List<User> expected = List.of(user);

        when(userRepository.getAll()).thenReturn(expected);

        List<User> actual = userService.getAllUsers();

        assertEquals(expected, actual);
    }

    @Test
    void getAllUsers_check_result_size() {
        User user = new User(1L, "login", "pass", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        List<User> expected = List.of(user);

        when(userRepository.getAll()).thenReturn(expected);

        List<User> actual = userService.getAllUsers();

        assertEquals(expected.size(), actual.size());
    }

    @Test
    void saveUser_check_call_repository_method_findByLogin() {
        User user = new User(1L, "login", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);
        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn("encode password");

        userService.saveUser(user);

        verify(userRepository).saveUser(user);
    }

    @Test
    void saveUser_if_user_with_this_login_isPresent() {
        User user = new User(1L, "login", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(user);

        assertThrows(UserAuthenticationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_login_is_null() {
        User user = new User(1L, null, "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_login_is_empty() {
        User user = new User(1L, "", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_login_length_less_than_3() {
        User user = new User(1L, "us", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_password_length_less_than_6() {
        User user = new User(1L, "name", "pass", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_password_is_null() {
        User user = new User(1L, "name", null, Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_password_is_empty() {
        User user = new User(1L, "name", "", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_firstName_is_null() {
        User user = new User(1L, "name", "password", Role.USER, null,
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_firstName_is_empty() {
        User user = new User(1L, "name", "password", Role.USER, "",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_secondName_is_null() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                null, Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_secondName_is_empty() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_gender_is_null() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", null, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_when_user_birthdayDate_is_null() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, null);

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_check_set_role_to_newUser() {
        User user = new User(1L, "name", "password", null, "first name",
                "second name", Gender.MALE, LocalDate.now());
        Role expected = Role.USER;

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);
        userService.saveUser(user);

        Role actual = user.getRole();

        assertEquals(expected, actual);
    }

    @Test
    void saveUser_check_encode_password() {
        User user = new User(1L, "name", "password", null, "first name",
                "second name", Gender.MALE, LocalDate.now());
        String expectedPassword = "encode password";

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);
        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn(expectedPassword);

        userService.saveUser(user);

        String actualPassword = user.getPassword();

        assertEquals(expectedPassword, actualPassword);
    }

    @Test
    void saveUser_check_call_method_toSave() {
        User user = new User(1L, "name", "password", null, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        userService.saveUser(user);

        verify(userRepository).saveUser(user);
    }

    @Test
    void success_save_user() {
        User user = new User(1L, "name", "password", null, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertDoesNotThrow(() -> userService.saveUser(user));
    }

    @Test
    void update_call_checkedMethod() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(user);

        userService.updateUser(1L, user);

        verify(userRepository).updateUser(1L, user);
    }

    @Test
    void update_when_user_not_found() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        when(userRepository.findById(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, user));
    }

    @Test
    void update_when_user_without_role() {
        User user = new User(1L, "name", "password", null, "first name",
                "second name", Gender.MALE, LocalDate.now());
        when(userRepository.findById(1L)).thenReturn(user);

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, user));
    }

    @Test
    void update_when_user_firstName_is_null() {
        User user = new User(1L, "name", "password", Role.USER, null,
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(user);

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, user));
    }

    @Test
    void update_when_user_firstName_is_empty() {
        User user = new User(1L, "name", "password", Role.USER, "",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(user);

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, user));
    }

    @Test
    void update_when_user_secondName_is_null() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                null, Gender.MALE, LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(user);

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, user));
    }

    @Test
    void update_when_user_secondName_is_empty() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "", Gender.MALE, LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(user);

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, user));
    }

    @Test
    void update_when_user_gender_is_null() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", null, LocalDate.now());

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.saveUser(user));
    }

    @Test
    void update_when_user_birthdayDate_is_null() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, null);

        when(userRepository.findById(1L)).thenReturn(user);

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, user));
    }

    @Test
    void update_check_call_method_toSave() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(user);

        userService.updateUser(1L, user);

        verify(userRepository).updateUser(1L, user);
    }

    @Test
    void success_update_user() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(user);

        assertDoesNotThrow(() -> userService.updateUser(1L, user));
    }

    @Test
    void changePassword_if_newPassword_is_null() {
        Long userId = 1L;
        String oldPassword = "password";
        String newPassword = null;

        assertThrows(ValidationException.class, () ->
                userService.changePassword(userId, oldPassword, newPassword));
    }

    @Test
    void changePassword_if_newPassword_is_empty() {
        Long userId = 1L;
        String oldPassword = "password";
        String newPassword = "";

        assertThrows(ValidationException.class, () ->
                userService.changePassword(userId, oldPassword, newPassword));
    }

    @Test
    void changePassword_if_newPassword_length_less_than_6() {
        Long userId = 1L;
        String oldPassword = "password";
        String newPassword = "pass";

        assertThrows(ValidationException.class, () ->
                userService.changePassword(userId, oldPassword, newPassword));
    }

    @Test
    void changePassword_check_call_findUser_method() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        Long userId = 1L;
        String oldPassword = "password";
        String newPassword = "password1";

        when(userRepository.findById(1L)).thenReturn(user);
        when(bCryptPasswordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        userService.changePassword(userId, oldPassword, newPassword);

        verify(userRepository).findById(1L);
    }

    @Test
    void changePassword_if_user_not_found() {
        Long userId = 1L;
        String oldPassword = "password";
        String newPassword = "password1";

        when(userRepository.findById(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.changePassword(userId, oldPassword, newPassword));
    }

    @Test
    void changePassword_check_equals_old_password_when_fail() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        Long userId = 1L;
        String oldPassword = "wrongPass";
        String newPassword = "password1";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String userEncodePassword = encoder.encode(user.getPassword());

        when(userRepository.findById(1L)).thenReturn(user);
        when(bCryptPasswordEncoder.matches(oldPassword, user.getPassword())).
                thenReturn(encoder.matches(oldPassword, userEncodePassword));

        assertThrows(WrongPasswordException.class, () ->
                userService.changePassword(userId, oldPassword, newPassword));
    }

    @Test
    void changePassword_check_equals_old_password_when_success() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        Long userId = 1L;
        String oldPassword = "password";
        String newPassword = "password1";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String userEncodePassword = encoder.encode(user.getPassword());

        boolean exceptedCheckPassword = true;
        boolean actualCheckPassword = encoder.matches(oldPassword, userEncodePassword);

        when(userRepository.findById(1L)).thenReturn(user);
        when(bCryptPasswordEncoder.matches(oldPassword, user.getPassword())).
                thenReturn(actualCheckPassword);

        userService.changePassword(userId, oldPassword, newPassword);

        assertEquals(exceptedCheckPassword, actualCheckPassword);
    }

    @Test
    void changePassword_check_call_encoder() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        Long userId = 1L;
        String oldPassword = "password";
        String newPassword = "password1";

        when(userRepository.findById(1L)).thenReturn(user);
        when(bCryptPasswordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        userService.changePassword(userId, oldPassword, newPassword);

        verify(bCryptPasswordEncoder).matches(oldPassword, user.getPassword());
    }

    @Test
    void changePassword_check_call_repository_method_to_call() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        Long userId = 1L;
        String oldPassword = "password";
        String newPassword = "password1";

        when(userRepository.findById(1L)).thenReturn(user);
        when(bCryptPasswordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        userService.changePassword(userId, oldPassword, newPassword);

        verify(userRepository).changePassword(1L, bCryptPasswordEncoder.encode(newPassword));
    }

    @Test
    void success_change_password() {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        Long userId = 1L;
        String oldPassword = "password";
        String newPassword = "password1";

        when(userRepository.findById(1L)).thenReturn(user);
        when(bCryptPasswordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        assertDoesNotThrow(() -> userService.changePassword(userId, oldPassword, newPassword));
    }

    @Test
    void checkPassword_if_different_pass() {
        String password = "pass";
        String confirmPassword = "password";
        bindingResult = mock(BindingResult.class);

        userService.checkPassword(password, confirmPassword, bindingResult);

        verify(bindingResult).rejectValue(
                "confirmPassword",
                "error.user",
                "New password and confirmation password do not match"
        );
    }

    @Test
    void password_is_equals() {
        String password = "password";
        String confirmPassword = "password";
        bindingResult = mock(BindingResult.class);

        assertDoesNotThrow(() -> userService.checkPassword(password, confirmPassword, bindingResult));
    }
}

