package com.Graduation.InstaCv.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserDetailsInfo {
    private Long id;
    private String email;
    private String password;
    // TODO: Add roles from DB, currently hardcoded to ROLE_USER, and add role on registration
    private List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_USER"));
}
