package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Modelo.Debts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtsRespository extends JpaRepository<Debts,Integer> {
    public List<Debts> findDebtsByUsuarioId(Integer id);
}
