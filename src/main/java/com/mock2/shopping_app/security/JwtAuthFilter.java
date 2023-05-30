package com.mock2.shopping_app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mock2.shopping_app.exception.ExceptionResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final Logger logger = Logger.getLogger(JwtAuthFilter.class);
    @Value("${app.jwt.header}")
    private String tokenRequestHeader;
    @Value("${app.jwt.prefix}")
    private String tokenRequestPrefix;
    @Autowired
    private JwtTokenValidator jwtTokenValidator;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = parseJwtTokenFromRequest(request);
            if (jwtToken != null && jwtTokenValidator.validateJwtToken(jwtToken)) {
                String email = jwtTokenProvider.getEmailFromJwtToken(jwtToken);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthoritiesFromJwtToken(jwtToken);
                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(userDetails, jwtToken, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Failed to set user authentication in security context: " + ex.getMessage());
            ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                    .status(HttpStatus.NOT_ACCEPTABLE.value())
                    .error(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                    .path(request.getRequestURI())
                    .message(ex.getMessage())
                    .timestamp(LocalDateTime.now()).build();

            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            // Serialize the ErrorResponse object to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.registerModule(new JavaTimeModule());
            String jsonErrorResponse = objectMapper.writeValueAsString(exceptionResponse);

            // Write the JSON response to the response body
            response.getWriter().write(jsonErrorResponse);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwtTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(tokenRequestHeader);
        if (StringUtils.hasText(header) && header.startsWith(tokenRequestPrefix)) {
            return header.substring(7);
        }
        return null;
    }
}
