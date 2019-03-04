package com.loya.devi.filter;

import com.loya.devi.security.JwtProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Validate jwt token and set context
 */
@Component
public class JwtAuthTokenFilter extends GenericFilterBean {
    private static final String CAN_NOT_SET_USER_AUTHENTICATION_MESSAGE = "Can NOT set user authentication -> Message: {}";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_ = "Bearer ";

    private JwtProvider tokenProvider;
    private UserDetailsService userDetailsService;

    public JwtAuthTokenFilter(JwtProvider jwtProvider, @Qualifier("userDetailsCustom") UserDetailsService userDetailsService) {
        this.tokenProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * method make token from request and check it
     * if token is not valid throw exception
     * if token is not exist just make doFilter()
     *
     * @param request  of user
     * @param response of app
     * @param chain    is filters chain
     * @throws IOException      if will exception
     * @throws ServletException if will exception
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String jwt = getJwt(httpRequest);
            logger.info(httpRequest.getRequestURL().toString());
            if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
                String username = tokenProvider.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error(CAN_NOT_SET_USER_AUTHENTICATION_MESSAGE, e);
            throw e;
        }

        chain.doFilter(request, response);
    }

    private String getJwt(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(BEARER_)) {
            return authHeader.replace(BEARER_, "");
        }

        return null;
    }
}