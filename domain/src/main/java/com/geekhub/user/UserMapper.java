package com.geekhub.user;

import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Locale;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("password"),
                Role.valueOf((rs.getString("role")).toUpperCase(Locale.ROOT)),
                rs.getString("first_name"),
                rs.getString("second_name"),
                Gender.valueOf((rs.getString("gender")).toUpperCase(Locale.ROOT)),
                LocalDate.parse(rs.getString("birthday_date"))
        );
    }

    public MapSqlParameterSource getParametersForAdd(User user) {
        return new MapSqlParameterSource(
                "login", user.getLogin())
                .addValue("password", user.getPassword())
                .addValue("role", String.valueOf(user.getRole()))
                .addValue("first_name", user.getFirstName())
                .addValue("second_name", user.getSecondName())
                .addValue("gender", String.valueOf(user.getGender()))
                .addValue("birthday_date", String.valueOf(user.getBirthdayDate()));
    }

    public MapSqlParameterSource getParametersForUpdate(Long id, User user) {
        return new MapSqlParameterSource(
                "id", id)
                .addValue("role", String.valueOf(user.getRole()))
                .addValue("first_name", user.getFirstName())
                .addValue("second_name", user.getSecondName())
                .addValue("gender", String.valueOf(user.getGender()))
                .addValue("birthday_date", String.valueOf(user.getBirthdayDate()));
    }

    public MapSqlParameterSource getParametersForChangePassword(Long id, String password) {
        return new MapSqlParameterSource(
                "id", id)
                .addValue("password", password);
    }
}
