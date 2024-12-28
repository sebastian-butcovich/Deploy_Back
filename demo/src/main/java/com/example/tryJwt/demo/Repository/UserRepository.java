package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Modelo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    public Optional<Users> findByEmail(String email);
    public Optional<Users> findByName(String name);
}
