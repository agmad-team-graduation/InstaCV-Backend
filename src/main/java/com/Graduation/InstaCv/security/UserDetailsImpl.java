package com.Graduation.InstaCv.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private final UserDetailsInfo userDetailsInfo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetailsInfo.getRoles();
    }

    @Override
    public String getPassword() {
        return userDetailsInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return userDetailsInfo.getEmail();
    }

    public Long getId() {
        return userDetailsInfo.getId();
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
}
