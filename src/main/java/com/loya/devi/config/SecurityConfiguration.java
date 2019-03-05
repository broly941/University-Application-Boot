package com.loya.devi.config;

import com.loya.devi.exception.JwtAuthEntryPoint;
import com.loya.devi.filter.JwtAuthTokenFilter;
import com.loya.devi.filter.LocaleFilter;
import com.loya.devi.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Autowired
    private JwtProvider jwtProvider;

    @Qualifier("userDetailsCustom")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private LocaleFilter localeFilter;
    /**
     * Bean for authenticate
     *
     * @return authenticationManagerBean
     * @throws Exception if will exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Bean for encoding password
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().
                authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers(HttpMethod.POST, "/groups", "/students", "/students/**", "/teachers", "/teachers/**").hasAnyRole("ADMIN", "PM")
                .antMatchers(HttpMethod.PUT, "/groups/**", "/students/**", "/teachers/**").hasAnyRole("ADMIN", "PM")
                .antMatchers(HttpMethod.DELETE, "/groups/**", "/students/**", "/teachers/**").hasRole("ADMIN")
                .anyRequest().hasAnyRole("USER", "ADMIN", "PM")
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(new JwtAuthTokenFilter(jwtProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(localeFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
