package com.geekhub.user;

import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
public class User implements UserDetails {

    private Long id;
    private String login;
    private String password;
    private Role role;
    private String firstName;
    private String secondName;
    private Gender gender;
    private LocalDate birthdayDate;


    public User(Long id, String login, String password, Role role, String firstName, String secondName, Gender gender, LocalDate birthdayDate) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.secondName = secondName;
        this.gender = gender;
        this.birthdayDate = birthdayDate;
    }

    public User() {
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
