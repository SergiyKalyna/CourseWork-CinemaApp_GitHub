package com.geekhub.user;

import com.geekhub.exception.UserNotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE id= :id",
                new MapSqlParameterSource("id", id));
    }

    public User findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id= :id",
                        new MapSqlParameterSource("id", id), userRowMapper)
                .stream()
                .findAny()
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users ORDER BY id ASC", userRowMapper)
                .stream()
                .toList();
    }

    public User findByLogin(String login) {
        return jdbcTemplate.query("SELECT * FROM users WHERE login= :login",
                        new MapSqlParameterSource("login", login), userRowMapper)
                .stream()
                .findAny().orElse(null);
    }

    public void saveUser(User user) {
        jdbcTemplate.update("INSERT INTO users (login,password,role,first_name,second_name,gender," +
                        "birthday_date) VALUES (:login,:password,:role,:first_name,:second_name,:gender,:birthday_date)",
                userRowMapper.getParametersForAdd(user));
    }

    public void updateUser(Long id, User user) {
        jdbcTemplate.update("UPDATE users SET role= :role,first_name= :first_name,second_name= :second_name," +
                "gender= :gender, birthday_date= :birthday_date WHERE id= :id", userRowMapper.getParametersForUpdate(id, user));
    }

    public void changePassword(Long id, String password) {
        jdbcTemplate.update("UPDATE users SET password= :password WHERE id= :id",
                userRowMapper.getParametersForChangePassword(id, password));
    }
}
