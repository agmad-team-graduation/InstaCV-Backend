package com.Graduation.InstaCv.config;

import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.service.Interfaces.IUserService;
import com.Graduation.InstaCv.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"dev", "dev-remote-ai"})
public class InitialUserLoader implements CommandLineRunner {
    private final UserService userService;
    @Value("${app.initial.user.email}")
    private String initEmail;
    @Value("${app.initial.user.password}")
    private String initPassword;
    @Value("${app.initial.user.name}")
    private String initName;

    @Override
    public void run(String... args) {
        userService.createNewUser(
                initEmail,
                initName,
                initPassword
        );
    }
}
