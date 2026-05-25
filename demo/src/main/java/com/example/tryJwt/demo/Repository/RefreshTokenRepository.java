package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Modelo.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findByUserId(long id);
    void deleteByToken(String jwtToken);
    void deleteByUserId(long id);
}

