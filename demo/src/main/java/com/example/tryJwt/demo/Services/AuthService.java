package com.example.tryJwt.demo.Services;

import com.example.tryJwt.demo.FileRequest.Request.ChangePasswordRequest;
import com.example.tryJwt.demo.FileRequest.Request.LoginRequest;
import com.example.tryJwt.demo.FileRequest.Responses.LoginResponse;
import com.example.tryJwt.demo.FileRequest.UsuarioDto;
import com.example.tryJwt.demo.FileRequest.Responses.TokenResponse;
import com.example.tryJwt.demo.Mapper.RegisterRequestMapper;
import com.example.tryJwt.demo.Mapper.UsuarioMapper;
import com.example.tryJwt.demo.Modelo.Token;
import com.example.tryJwt.demo.Modelo.Usuario;
import com.example.tryJwt.demo.Repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.tryJwt.demo.Repository.RefreshTokenRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
@PropertySource("classpath:application.properties")
@Log4j2
public class AuthService {

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private  JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RegisterRequestMapper registerRequestMapper;

    @Autowired
    private UsuarioMapper usuarioMapper;


    @Transactional
    public TokenResponse register(UsuarioDto request) {
        log.debug("Register request received for user {}", request.email());
        if(usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("The email already exists");
        }
        // Proceso y guardo en user los valores recibidos en req
        Usuario user = registerRequestMapper.toEntity(request);
        user.setCreado(new Date());
        user.setUltimaModificacion(new Date());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setId(null);
        usuarioRepository.save(user);
        // Genero y guardo los tokens (si tokenRegistration == true)
        String refreshToken = jwtService.getRefreshToken(user);
        String accessToken = jwtService.getAccessToken(refreshToken, user);
        // Retorno los tokens generados para uso
        return new TokenResponse(accessToken,refreshToken);
    }

    @Transactional
    public LoginResponse login (LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        log.debug("Login request received for user {}", request.email());
        Optional<Usuario> user = usuarioRepository.findByEmail(request.email());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        UsuarioDto usuario = usuarioMapper.toDto(user.get());
        String refreshToken = jwtService.getRefreshToken(user.get());
        String accessToken = jwtService.getAccessToken(refreshToken, user.get());
        return LoginResponse.builder() // Creo el dto response con los datos del usuario logueado
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .email(usuario.email())
                .username(usuario.username())
                .firstname(usuario.firstname())
                .surname(usuario.surname())
                .create(user.get().getCreado())
                .foto(usuario.foto())
                .dineroActual(usuario.dineroActual())
                .build();

        /*log.debug("Se persiste el token? {}", tokenRegistration);
        if(tokenRegistration) { // Si se guardan los tokens en la BD
            // Obtengo el primer token valido y
            Optional<Token> savedRefreshToken = refreshTokenRepository.findRevokedIsFalseByUserId(user.get().getId());
            if (savedRefreshToken.isEmpty() || !jwtService.isTokenExpired(savedRefreshToken.get().getToken())) {
                // Si no existe token o está invalido creo uno nuevo, lo guardo y retorno
                log.debug("Token vacio/invalido; genera uno");
                jwtService.revokeUserRefreshToken(user.get());
                String newRefreshToken = jwtService.generateRefreshToken(user.get());
                jwtService.saveUserToken(user.get(), newRefreshToken);
                return returnLogin.toBuilder()
                        .refresh_token(newRefreshToken)
                        .build();
            } else {
                // Retorno token persistido
                return returnLogin.toBuilder()
                        .refresh_token(savedRefreshToken.get().getToken())
                        .build();
            }
        } else {
            // Genero el token y retorno
            log.debug("Genero token nuevo");
            return returnLogin.toBuilder()
                    .refresh_token(jwtService.generateRefreshToken(user.get()))
                    .build();
        }*/
    }

    @Transactional
    public TokenResponse refreshAccessToken(String expiredAccessToken, String refreshToken) {
        String userEmail = jwtService.extractEmail(expiredAccessToken);
        Optional<Usuario> user = usuarioRepository.findByEmail(userEmail);
        if(user.isEmpty()) {
            throw new UsernameNotFoundException("Username not found with email: " + userEmail);
        }
        if(!jwtService.isTokenExpired(refreshToken)) {
            // isTokenExpired verifica implicitamente si es un token firmado valido tambien
            throw new IllegalArgumentException("Invalid refresh token. Must login again");
        }
        if(!userEmail.equals(jwtService.extractEmail(refreshToken))) {
            throw new IllegalArgumentException("Refresh token owner does not correspond with access token owner");
        }
        log.debug("Refresh token received for user {}", userEmail);
        String newAccessToken = jwtService.getAccessToken(refreshToken, user.get());
        // Solución más simple que se me ocurrio para safar.
        // Esto a futuro debería cambiar.
        // bdgarat: Estaria arreglado con check si hay bearer. Comentada linea de abajo (deprecada)
        // refreshToken = jwtService.normalizeToken(refreshToken);
        return new TokenResponse(newAccessToken, refreshToken);
    }

    @Transactional
    public void changePassword(String token, ChangePasswordRequest req) {
        Usuario me = usuarioRepository.findByEmail(jwtService.extractEmail(token))
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el elemento con token: " + token));
        log.debug("Change password for user {}", me.getEmail());
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!me.getPassword().equals(encoder.encode(req.repeatNewPassword()))) {
            throw new IllegalArgumentException("La contraseña antigua no coincide con la provista");
        }
        if(!req.newPassword().equals(req.repeatNewPassword())) {
            throw new IllegalArgumentException("La contraseñas no coinciden");
        }
        me.setPassword(encoder.encode(req.newPassword()));
        usuarioRepository.save(me);
    }

}