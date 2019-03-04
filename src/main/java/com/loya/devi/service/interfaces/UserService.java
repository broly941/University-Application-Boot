package com.loya.devi.service.interfaces;

import com.loya.devi.controller.request.UserAuthParameters;
import com.loya.devi.controller.request.UserRegistrationParameters;
import com.loya.devi.controller.response.JwtResponse;
import com.loya.devi.entity.User;

/**
 * Service for class {@link User}
 *
 * @author ilya.korzhavin
 */
public interface UserService {
    User get(String username);

    User save(UserRegistrationParameters signUpRequest);

    JwtResponse getToken(UserAuthParameters loginRequest);

    JwtResponse refreshToken(String token);
}
