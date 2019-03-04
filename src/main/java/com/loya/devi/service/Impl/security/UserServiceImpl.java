package com.loya.devi.service.Impl.security;

import com.loya.devi.controller.request.UserAuthParameters;
import com.loya.devi.controller.request.UserRegistrationParameters;
import com.loya.devi.controller.response.JwtResponse;
import com.loya.devi.entity.Role;
import com.loya.devi.entity.User;
import com.loya.devi.exception.ValidationException;
import com.loya.devi.repository.RoleRepository;
import com.loya.devi.repository.UserRepository;
import com.loya.devi.security.JwtProvider;
import com.loya.devi.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link UserService} interface.
 * <p>
 * Create and manage of user entities
 *
 * @author ilya.korzhavin
 */
@Service
public class UserServiceImpl implements UserService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_PM = "ROLE_PM";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ADMIN_ROLE_NOT_FIND = "Admin Role not find";
    private static final String PM_ROLE_NOT_FIND = "PM Role not find";
    private static final String USER_ROLE_NOT_FIND = "User Role not find";

    private static final String ADMIN = "admin";
    private static final String PM = "pm";
    private static final String USERNAME_IS_ALREADY_TAKEN = "Username is already taken";
    private static final String EMAIL_IS_ALREADY_IN_USE = "Email is already in use";
    private static final String USER_NOT_FOUND = "User not found: ";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    /**
     * method return user entity
     *
     * @param username of entity
     * @return user entity
     */
    @Override
    public User get(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND + username));
    }

    /**
     * method registry new user and return it
     *
     * @param signUpRequest request user data
     * @return new user
     */
    @Override
    @Transactional
    public User save(UserRegistrationParameters signUpRequest) {
        validationRequestData(signUpRequest);
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()), getRoles(signUpRequest.getRoles()));
        return userRepository.save(user);
    }

    /**
     * method create token by user
     *
     * @param loginRequest request user data
     * @return token
     */
    @Override
    public JwtResponse getToken(UserAuthParameters loginRequest) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new JwtResponse(jwtProvider.generateJwtToken(authentication));
    }

    /**
     * method return refresh token
     *
     * @param token old access token
     * @return refresh token
     */
    @Override
    public JwtResponse refreshToken(String token) {
        String jwt = jwtProvider.generateRefreshToken(token);
        return new JwtResponse(jwt);
    }

    private Set<Role> getRoles(Set<String> requestRoles) {
        return requestRoles.stream()
                .map(role -> {
                    switch (role) {
                        case ADMIN:
                            return roleRepository.findByName(ROLE_ADMIN).orElseThrow(() -> new ValidationException(ADMIN_ROLE_NOT_FIND));
                        case PM:
                            return roleRepository.findByName(ROLE_PM).orElseThrow(() -> new ValidationException(PM_ROLE_NOT_FIND));
                        default:
                            return roleRepository.findByName(ROLE_USER).orElseThrow(() -> new ValidationException(USER_ROLE_NOT_FIND));
                    }
                })
                .collect(Collectors.toSet());
    }

    private void validationRequestData(UserRegistrationParameters signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new ValidationException(USERNAME_IS_ALREADY_TAKEN);
        } else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ValidationException(EMAIL_IS_ALREADY_IN_USE);
        }
    }

}
