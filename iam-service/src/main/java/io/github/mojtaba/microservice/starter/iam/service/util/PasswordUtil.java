package io.github.mojtaba.microservice.starter.iam.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUtil {

    private static final String PEPPER_KEY = "##c5b952a4c9dc##";
    private final PasswordEncoder passwordEncoder;

    public String hashPassword(String username, String password) {
        return passwordEncoder.encode(addPepperToPassword(username, password));
    }

    public boolean checkPassword(String username, String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(addPepperToPassword(username, rawPassword), hashedPassword);
    }

    private String addPepperToPassword(String username, String password) {
        return username.toLowerCase().concat(PEPPER_KEY).concat(password);
    }

}
