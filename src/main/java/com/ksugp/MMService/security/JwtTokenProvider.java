package com.ksugp.MMService.security;

import com.ksugp.MMService.model.Role;
import com.ksugp.MMService.model.SafeUser;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.*;

@Component
public class JwtTokenProvider {
    @Autowired
    private final UserDetailsService userDetailsService;
    @Value("${jwt.header}")
    private String authorizationHeader;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long validityInMilliseconds;
    @Value("${jwt.cookieName}")
    private String cookieName;

    public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    public String createToken(String username/*это email*/, String role, /*это username*/String actualUsername, Long id){
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);
        claims.put("actualUsername", actualUsername);
        claims.put("id", String.valueOf(id));
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public boolean validateToken(String token){
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch(JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid!!!", HttpStatus.UNAUTHORIZED);
        }
    }
    public Authentication getAuthentication(String token) throws UsernameNotFoundException{
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        }catch (UsernameNotFoundException e){//?
            return null;
        }
    }
    public String getUsername(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
    public SafeUser getFullClaimsInfoInSafeUserClass(String token){
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        SafeUser actualUser = new SafeUser();
        actualUser.setEmail(claims.getSubject());
        actualUser.setUsername(claims.get("actualUsername",String.class));
        actualUser.setRole(Role.valueOf(claims.get("role",String.class)));
        Long id = Long.valueOf(claims.get("id",String.class));
        actualUser.setId(id);
        return actualUser;
    }
    public String resolveToken(HttpServletRequest request){
        //return request.getHeader(authorizationHeader); //для хранения токена в хедере
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        return cookie != null ? cookie.getValue() : null;
    }
}
