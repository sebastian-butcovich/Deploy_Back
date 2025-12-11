package com.example.tryJwt.demo.Config;

import com.example.tryJwt.demo.Modelo.Token;
import com.example.tryJwt.demo.Modelo.Usuario;
import com.example.tryJwt.demo.Repository.TokenRepository;
import com.example.tryJwt.demo.Repository.UsuarioRepository;
import com.example.tryJwt.demo.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
@Service
@PropertySource("classpath:application.properties")
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private  UserDetailsService userDetailsService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${jwt.token.registration}")
    private boolean tokenRegistration;


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String userEmail;
        try {
            userEmail = jwtService.extractEmail(request.getHeader(HttpHeaders.AUTHORIZATION));
        } catch (IllegalArgumentException e) {
            filterChain.doFilter(request, response);
            return;
        }
        if(userEmail == null || SecurityContextHolder.getContext().getAuthentication()!=null) {
            return;
        }
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        Optional<Usuario> user = usuarioRepository.findByEmail(userDetails.getUsername());
        if(user.isEmpty()) {
            filterChain.doFilter(request,response);
            return;
        }
        if(tokenRegistration) {
            Token token = tokenRepository.findByToken(request.getHeader(HttpHeaders.AUTHORIZATION).substring(7)).orElse(null);
            if (token == null || token.isExpired() || token.isRevoked()) {
                filterChain.doFilter(request, response);
            }
        } else {
            if(!jwtService.isValidToken(request.getHeader(HttpHeaders.AUTHORIZATION), user.get())) {
                filterChain.doFilter(request,response);
                return;
            }
        }
        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/") || path.equals("/api/health");
    }
}

