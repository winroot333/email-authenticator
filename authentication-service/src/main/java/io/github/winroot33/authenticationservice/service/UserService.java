package io.github.winroot33.authenticationservice.service;

import io.github.winroot33.authenticationservice.entity.User;
import io.github.winroot33.authenticationservice.exception.UserAlreadyExistsException;
import io.github.winroot33.authenticationservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public String handleRegistration(String email, String encodedPassword) {
        if (userRepository.findByEmail(email).isPresent()){
            throw new UserAlreadyExistsException("User already exists with email: " + email);
        }
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .build();

        userRepository.save(user);
        return null;
    }

    public String handleLogin(String email, String encodedPassword) {
        return null;
    }
}
