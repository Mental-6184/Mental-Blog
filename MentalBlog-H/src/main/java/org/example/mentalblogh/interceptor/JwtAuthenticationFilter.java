package org.example.mentalblogh.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.mentalblogh.common.utils.JwtUtil;
import org.example.mentalblogh.common.utils.TokenSecurityService;
import org.example.mentalblogh.mapper.UserMapper;
import org.example.mentalblogh.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenSecurityService tokenSecurity;
    private final UserMapper userMapper;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, TokenSecurityService tokenSecurity, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.tokenSecurity = tokenSecurity;
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (StringUtils.hasText(token) && jwtUtil.validateToken(token) && !tokenSecurity.isRevoked(token)) {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            User user = userMapper.selectById(userId);
            if (user == null || Integer.valueOf(1).equals(user.getStatus())) {
                filterChain.doFilter(request, response);
                return;
            }
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            // 构建认证信息
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(authority));
            authentication.setDetails(username);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
