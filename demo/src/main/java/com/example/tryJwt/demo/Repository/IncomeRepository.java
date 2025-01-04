package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Modelo.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income,Integer> {
    @Query("select s from Income s where s.usuario.id = ?1")
    public Page<Income> findAllByUsuario(Integer id, Pageable pageable);
    @Query("select s from Income s where s.usuario.id = ?1")
    public List<Income> findAllByUsuario(Integer id);
}
