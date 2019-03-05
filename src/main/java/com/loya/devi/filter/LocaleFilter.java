package com.loya.devi.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;

/**
 * If the request has the wrong Accept-Language or Locale
 * is not available then throw 404 error.
 *
 * @author DEVIAPHAN on 19.12.2018
 * @project university
 */
@Component
public class LocaleFilter extends GenericFilterBean {

    private static Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);

    private final Predicate<Integer> indexValidPredicate = index -> index != -1;

    private static final String LANGUAGE_NOT_SUPPORTED = "Language not supported: ";
    private static final String LOCALE_EN = "en-US";
    private static final String PROPERTIES = ".properties";
    private static final String HTTP_STATUS_400_LANGUAGE_NOT_SUPPORTED = "HTTP Status 400 – Language not supported: ";
    private static final String REGEX = "(?<=\\_).+?(?=\\.)";
    private static final String COMMA = ",";
    private static final String MINUS = "-";
    private static final String UNDERSCORE = "_";
    private static final String EMPTY = "";

    private List<String> langList;


    @Override
    public void initFilterBean() {
        langList = loadLanguageList();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        logger.info(httpRequest.getRequestURL().toString());
        String acceptLanguage = httpRequest.getHeader(ACCEPT_LANGUAGE);
        if (acceptLanguage == null) {
            setLocale(httpRequest, httpServletResponse, chain, LOCALE_EN);
        } else {
            acceptLanguage = substring(httpRequest.getHeader(ACCEPT_LANGUAGE), COMMA);
            try {
                setLanguage(httpRequest, httpServletResponse, chain, acceptLanguage);
            } catch (IOException | ServletException e) {
                httpServletResponse.sendError(400, e.getMessage());
            }
        }
    }

    private void setLocale(HttpServletRequest request, HttpServletResponse response, FilterChain
            filterChain, String acceptLanguage) throws IOException, ServletException {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LocaleContextHolder.setLocale(locale);
        WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
        filterChain.doFilter(request, response);
    }

    private String substring(String value, String delimiter) {
        int index = value.indexOf(delimiter);
        return indexValidPredicate.test(index) ? value.substring(0, index) : value;
    }

    private void setLanguage(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String acceptLanguage) throws IOException, ServletException {
        if (langList.contains(acceptLanguage.replace(MINUS, UNDERSCORE))) {
            setLocale(request, response, filterChain, acceptLanguage);
        } else if (!getLocaleByLanguage(request, response, filterChain, acceptLanguage)) {
            logger.error(HTTP_STATUS_400_LANGUAGE_NOT_SUPPORTED + acceptLanguage);
//            response.sendError(400, LANGUAGE_NOT_SUPPORTED + acceptLanguage);
            setLocale(request, response, filterChain, LOCALE_EN);
        }
    }

    private boolean getLocaleByLanguage(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String acceptLanguage) throws IOException, ServletException {
        String language = substring(acceptLanguage, MINUS);
        for (String value : langList) {
            if (substring(value, UNDERSCORE).contains(language)) {
                setLocale(request, response, filterChain, value.replace(UNDERSCORE, MINUS));
                return true;
            }
        }
        return false;
    }

    private List<String> loadLanguageList() {
        Pattern pattern = Pattern.compile(REGEX);
        try {
            Path paths = Paths.get(Objects.requireNonNull(this.getClass().getClassLoader().getResource(EMPTY)).toURI());

            return Files.list(paths)
                    .filter(path -> path.getFileName().toString().endsWith(PROPERTIES))
                    .map(path -> {
                        Matcher matcher = pattern.matcher(path.getFileName().toString());
                        if (matcher.find()) {
                            return matcher.group(0);
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            return null;
        }
    }
}