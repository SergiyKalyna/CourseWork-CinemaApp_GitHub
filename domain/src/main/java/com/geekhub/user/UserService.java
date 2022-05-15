package com.geekhub.user;

import com.geekhub.exception.UserAuthenticationException;
import com.geekhub.exception.UserNotFoundException;
import com.geekhub.exception.ValidationException;
import com.geekhub.exception.WrongPasswordException;
import com.geekhub.models.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findById(Long id) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(id));
        logger.info("Found user with id -" + id);

        return user.orElseThrow(() -> new UserNotFoundException(id));
    }

    public void deleteUser(Long id) {
        if (userRepository.findById(id) == null) {
            throw new UserNotFoundException(id);
        }
        userRepository.delete(id);
        logger.info("Deleted user with id -" + id);
    }

    public List<User> getAllUsers() {
        logger.info("Showed all users");
        return userRepository.getAll();
    }

    public void saveUser(User user) {
        User userFromDB = userRepository.findByLogin(user.getLogin());
        if (userFromDB != null) {
            throw new UserAuthenticationException(user.getLogin());
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().length() < 3) {
            throw new ValidationException("Was input empty login or login have less than 3 symbols");
        } else if (user.getPassword() == null || user.getPassword().isBlank() || user.getPassword().length() < 6) {
            throw new ValidationException("Was input empty password or password have less than 6 symbols");
        }
        checkUserData(user);

        user.setRole(Role.USER);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.saveUser(user);

        logger.info("Was added new user");
    }

    public void updateUser(Long id, User user) {
        if (userRepository.findById(id) == null) {
            throw new UserNotFoundException(id);
        } else if (user.getRole() == null) {
            throw new ValidationException("Was not choice the role");
        }
        checkUserData(user);

        userRepository.updateUser(id, user);

        logger.info("Was updated user with id -" + id);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 6) {
            throw new ValidationException("New password is empty or new password have less than 6 symbols");
        }
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        String databasePassword = user.getPassword();
        boolean checkPassword = bCryptPasswordEncoder.matches(oldPassword, databasePassword);

        if (checkPassword) {
            userRepository.changePassword(userId, bCryptPasswordEncoder.encode(newPassword));
            logger.info("User password was changed");
        } else {
            throw new WrongPasswordException("Was input wrong password");
        }
    }

    private void checkUserData(User user) {
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new ValidationException("Was input empty first name field");
        } else if (user.getSecondName() == null || user.getSecondName().isBlank()) {
            throw new ValidationException("Was input empty second name field");
        } else if (user.getGender() == null) {
            throw new ValidationException("Was not choice gender field");
        } else if (user.getBirthdayDate() == null) {
            throw new ValidationException("Was fill birthday date field");
        }
    }
}
