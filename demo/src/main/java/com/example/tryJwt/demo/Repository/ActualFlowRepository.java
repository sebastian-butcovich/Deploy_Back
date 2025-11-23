package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Modelo.ActualFlow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActualFlowRepository extends CrudRepository<ActualFlow,Integer> {
    //Busqueda completa sin filtros
    @Query(value = "select * from spent  where id_autor = ?1 order by id desc",nativeQuery = true)
    public List<ActualFlow> findAllByUsuario(Integer id);
    //Busquda con filtro monto solamente
    @Query(value = "select * from spent where id_autor  = ?1 and monto between ?2 and ?3 order by id desc", nativeQuery = true)
    public List<ActualFlow> findAllByUsuario(Integer id, Double monto_min, Double monto_max);
    //Busqueda con filtro monto y tipo
    @Query(value = "select * from spent where id_autor  = ?1  and tipo = ?2 order by id desc", nativeQuery = true)
    public List<ActualFlow> findAllByUsuario(Integer id, String tipo);
    @Query(value = "select * from spent where id_autor  = ?1  and fecha between ?2 and ?3 order by id desc", nativeQuery = true)
    public List<ActualFlow> findAllByUsuario(Integer id, String fecha_inicio, String fecha_final);
    @Query(value = "select * from spent where id_autor  = ?1  and fecha between ?2 and ?3 order by fecha", nativeQuery = true)
    public List<ActualFlow> findAllByUsuarioFecha(Integer id, String fecha_inicio, String fecha_final);
}
