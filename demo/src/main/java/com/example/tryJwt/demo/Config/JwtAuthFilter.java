package com.example.tryJwt.demo.Config;

import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Modelo.Token;
import com.example.tryJwt.demo.Repository.TokenRepository;
import com.example.tryJwt.demo.Repository.UserRepository;
import com.example.tryJwt.demo.Servicies.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
@Service
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private  UserDetailsService userDetailsService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().contains("/auth"))
        {
            filterChain.doFilter(request,response);
            return ;
        }

        String authHeader = request.getParameter("token");
        if(authHeader== null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request,response);
            return;
        }
        String jwtToken = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(jwtToken);
        if(userEmail == null || SecurityContextHolder.getContext().getAuthentication()!=null)
        {
            return;
        }
        Token token = tokenRepository.findByToken(jwtToken).orElse(null);
        if(token ==null || token.isExpired() || token.isRevoked())
        {
            filterChain.doFilter(request,response);
        }
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        Optional<Users> user = userRepository.findByEmail(userDetails.getUsername());
        if(user.isEmpty())
        {
            filterChain.doFilter(request,response);
            return;
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
}
