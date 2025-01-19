package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Modelo.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpentRepository extends CrudRepository<Spent,Integer> {
    //Busqueda completa sin filtros
    @Query(value = "select * from spent  where id_autor = ?1 order by id asc",nativeQuery = true)
    public Page<Spent> findAllByUsuario(Integer id, Pageable pageable);
    //Busquda con filtro monto solamente
    @Query(value = "select * from spent where id_autor  = ?1 and monto between ?2 and ?3  order by id desc", nativeQuery = true)
    public Page<Spent> findAllByUsuario(Integer id,Double monto_min, Double monto_max, Pageable pageable);
    //Busqueda con filtro monto y tipo
    @Query(value = "select * from spent where id_autor  = ?1  and tipo = ?2  order by id desc", nativeQuery = true)
    public Page<Spent> findAllByUsuario(Integer id, String tipo, Pageable pageable);
    @Query(value = "select * from spent where id_autor  = ?1  and fecha between ?2 and ?3  order by id desc", nativeQuery = true)
    public Page<Spent> findAllByUsuario(Integer id,  String fecha_inicio, String fecha_final, Pageable pageable);

    @Query("select s from Spent s where s.usuario.id = ?1")
    public List<Spent> findAllByUsuario(Integer id);

}
