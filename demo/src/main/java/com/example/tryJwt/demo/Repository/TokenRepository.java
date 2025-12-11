package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Modelo.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findValidIsFalseOrRevokedIsFalseByUserId(long id);
    Optional<Token> findByToken(String jwtToken);
}

