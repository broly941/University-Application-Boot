package com.loya.devi.filter;

import com.loya.devi.security.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.persistence.EntityNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Validate jwt token and set context
 */
public class JwtAuthTokenFilter extends OncePerRequestFilter {
    private static final String CAN_NOT_SET_USER_AUTHENTICATION_MESSAGE = "Can not set user authentication -> Message: {}";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_ = "Bearer ";

    private static Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);

    private JwtProvider tokenProvider;
    private UserDetailsService userDetailsService;

    public JwtAuthTokenFilter(JwtProvider jwtProvider, @Qualifier("userDetailsCustom") UserDetailsService userDetailsService) {
        this.tokenProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, FilterChain chain) throws ServletException, IOException {
        String jwt = getJwt(httpRequest);
        logger.info(httpRequest.getRequestURL().toString());
        try {
            if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
                String username = tokenProvider.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
//            logger.error(CAN_NOT_SET_USER_AUTHENTICATION_MESSAGE, e);
            throw e;
        }

        chain.doFilter(httpRequest, httpServletResponse);
    }

    private String getJwt(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(BEARER_)) {
            return authHeader.replace(BEARER_, "");
        }

        return null;
    }
}