package com.loya.devi.controller;

import com.loya.devi.controller.request.UserAuthParameters;
import com.loya.devi.controller.request.UserRegistrationParameters;
import com.loya.devi.controller.response.JwtResponse;
import com.loya.devi.entity.User;
import com.loya.devi.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for registration and authorization user
 *
 * @author ilya.korzhavin
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    /**
     * Create and return token by name and password user
     *
     * @param loginRequest request user data
     * @return token
     */
    @PostMapping("/signin")
    public JwtResponse authenticateUser(@RequestBody UserAuthParameters loginRequest) {
        return userService.getToken(loginRequest);
    }

    /**
     * Refresh jwt and return it
     *
     * @return token
     */
    @PostMapping("/refresh")
    public JwtResponse refreshToken(@RequestParam String token) {
        return userService.refreshToken(token);
    }

    /**
     * Create new user account and return it
     *
     * @param signUpRequest request user data
     * @return new user data
     */
    @PostMapping("/signup")
    public User registerUser(@RequestBody UserRegistrationParameters signUpRequest) {
        return userService.save(signUpRequest);
    }
}
