package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Modelo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpentRepository extends JpaRepository<Spent,Integer> {
    @Query("select s from Spent s where s.usuario.id = ?1")
    public List<Spent> findAllByUsuario(Integer id);

}
