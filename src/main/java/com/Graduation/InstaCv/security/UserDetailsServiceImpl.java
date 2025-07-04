package com.Graduation.InstaCv.security;

import com.Graduation.InstaCv.data.enums.AuthProvider;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.exceptions.InvalidCredentialsException;
import com.Graduation.InstaCv.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        // Check if user is an OAuth user trying to login with email/password
        if (user.getAuthProvider() != null && user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new InvalidCredentialsException("This account was created using a social login provider. Please use the social login option.");
        }
        
        return new UserDetailsImpl(UserDetailsInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .build()
        );
    }
}
