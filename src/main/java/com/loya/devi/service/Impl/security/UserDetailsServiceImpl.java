package com.loya.devi.service.Impl.security;

import com.loya.devi.service.Impl.security.component.UserDetailsImpl;
import com.loya.devi.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserDetailsService} interface.
 * <p>
 * Add access right for current user.
 *
 * @author ilya.korzhavin
 */
@Service("userDetailsCustom")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    /**
     * method return UserDetails by username
     *
     * @param username of user
     * @return UserDetails object
     * @throws UsernameNotFoundException if user not exists
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return UserDetailsImpl.build(userService.get(username));
    }
}
