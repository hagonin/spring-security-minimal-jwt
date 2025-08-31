package com.example.demo.services;

import com.example.demo.models.UserApp;
import com.example.demo.repositories.UserAppRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Service for handling JWT token operations and authentication.
 * Extends OncePerRequestFilter to process JWT tokens from HTTP cookies on each request.
 * Provides token generation, validation, and authentication setup.
 */
@Service
public class JwtService extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private static String SECRET;
    /**
     * Sets the JWT secret key from application properties.
     * 
     * @param secret the secret key for JWT signing
     */
    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        SECRET = secret;
    }

    private static String COOKIE_NAME= "";
    /**
     * Sets the JWT cookie name from application properties.
     * 
     * @param cookie_name the name of the JWT cookie
     */
    @Value("${jwt.cookie_name}")
    public void setCookieName(String cookie_name) {
        COOKIE_NAME = cookie_name;
    }
    /** JWT token validity period in milliseconds (5 hours) */
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    @Autowired
    UserAppRepository userAppRepository;

    /**
     * Filters incoming requests to extract and validate JWT tokens from cookies.
     * Sets up Spring Security authentication context if token is valid.
     * 
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @param filterChain the filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getCookies() != null) {
            Stream.of(request.getCookies())
                    .filter(cookie ->
                            cookie.getName().equals(COOKIE_NAME))
                                .map(Cookie::getValue)
                    .forEach(token -> {
                        try {

                            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();

                            Optional<UserApp> optUserApp = userAppRepository.findByUsername(claims.getSubject());
                            if(optUserApp.isEmpty()){
                                throw new UsernameNotFoundException(claims.getSubject());
                            }
                            UserApp userApp = optUserApp.get();

                            if (validateToken(token, userApp)) {
                                String role = claims.get("role", String.class);
                                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                                if (role != null) {
                                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                                }
                                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                        userApp, null, authorities);
                                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                            }
                        } catch (Exception e) {
                            // Remove the cookie
                            Cookie expiredCookie = new Cookie(COOKIE_NAME, null);
                            expiredCookie.setPath("/");
                            expiredCookie.setHttpOnly(true);
                            expiredCookie.setMaxAge(0); // Set the cookie's max age to 0 to delete it
                            response.addCookie(expiredCookie);
                        }
                    });
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Validates a JWT token by parsing it with the secret key.
     * 
     * @param token the JWT token to validate
     * @param userApp the user associated with the token
     * @return true if token is valid, false otherwise
     */
    public static Boolean validateToken(String token, UserApp userApp) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        }catch (Exception e){
            return false;
        }
        return true;

    }

    /**
     * Generates a JWT token for a user with username and role claims.
     * 
     * @param userApp the user for whom to generate the token
     * @return the generated JWT token string
     */
    public static String generateToken(UserApp userApp) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userApp.getUsername());
        claims.put("role", userApp.getRole().name());
        return Jwts.builder().setClaims(claims).setSubject(userApp.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
    }

    /**
     * Creates an HTTP-only authentication cookie containing a JWT token.
     * 
     * @param userApp the user for whom to create the authentication token
     * @return ResponseCookie with the JWT token
     * @throws Exception if token creation fails or user is disabled
     */
    public ResponseCookie createAuthenticationToken(UserApp userApp ) throws Exception {
        try {
            final String token = generateToken(userApp);
            return ResponseCookie.from(COOKIE_NAME, token).httpOnly(true)
                    .path("/").build();
        }catch(DisabledException e) {
            throw new Exception();
        }

    }
}
