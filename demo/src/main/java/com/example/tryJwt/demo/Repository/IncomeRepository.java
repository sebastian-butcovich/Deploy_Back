package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Modelo.Income;
import com.example.tryJwt.demo.Modelo.Spent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income,Integer> {
    @Query(value = "select * from income  where id_autor = ?1 order by id desc",nativeQuery = true)
    public Page<Income> findAllByUsuario(Integer id, Pageable pageable);
    @Query("select s from Income s where s.usuario.id = ?1  order by id desc")
    public List<Income> findAllByUsuario(Integer id);
    @Query(value = "select * from spent where id_autor  = ?1 and monto between ?2 and ?3  order by id desc", nativeQuery = true)
    public Page<Income> findAllByUsuario(Integer id, Double monto_min, Double monto_max, Pageable pageable);
    //Busqueda con filtro monto y tipo
    @Query(value = "select * from spent where id_autor  = ?1  and tipo = ?2  order by id desc", nativeQuery = true)
    public Page<Income> findAllByUsuario(Integer id, String tipo, Pageable pageable);
    @Query(value = "select * from spent where id_autor  = ?1  and fecha between ?2 and ?3  order by id desc", nativeQuery = true)
    public Page<Income> findAllByUsuario(Integer id,  String fecha_inicio, String fecha_final, Pageable pageable);
}
