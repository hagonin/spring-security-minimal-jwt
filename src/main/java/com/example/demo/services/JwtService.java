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

@Service
public class JwtService extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private static String SECRET;
    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        SECRET = secret;
    }

    private static String COOKIE_NAME= "";
    @Value("${jwt.cookie_name}")
    public void setCookieName(String cookie_name) {
        COOKIE_NAME = cookie_name;
    }
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    @Autowired
    UserAppRepository userAppRepository;

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
                                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                        userApp, null, null);
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

    public static Boolean validateToken(String token, UserApp userApp) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        }catch (Exception e){
            return false;
        }
        return true;

    }

    public static String generateToken(UserApp userApp) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userApp.getUsername());
        return Jwts.builder().setClaims(claims).setSubject(userApp.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
    }

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
