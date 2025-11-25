package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Enums.TipoActualFlow;
import com.example.tryJwt.demo.Modelo.ActualFlow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActualFlowRepository extends CrudRepository<ActualFlow,Integer> {
    //Busqueda completa sin filtros
    @Query(value = "select * from spent  where id_autor = ?1 and tipo = ?2 order by id desc",nativeQuery = true)
    public List<ActualFlow> findAllByUsuario(Integer id, TipoActualFlow tipo);
    //Busquda con filtro monto solamente
    @Query(value = "select * from spent where id_autor  = ?1 and tipo = ?2 and monto between ?3 and ?4 order by id desc", nativeQuery = true)
    public List<ActualFlow> findAllByUsuario(Integer id, TipoActualFlow tipo, Double monto_min, Double monto_max);
    //Busqueda con filtro monto y tipo
    @Query(value = "select * from spent where id_autor  = ?1 and tipo = ?2 and subtipo = ?3 order by id desc", nativeQuery = true)
    public List<ActualFlow> findAllByUsuario(Integer id, TipoActualFlow tipo, String subtipo);
    @Query(value = "select * from spent where id_autor  = ?1  and tipo = ?2 and fecha between ?3 and ?4 order by id desc", nativeQuery = true)
    public List<ActualFlow> findAllByUsuario(Integer id, TipoActualFlow tipo, String fecha_inicio, String fecha_final);
    @Query(value = "select * from spent where id_autor  = ?1  and tipo = ?2 and fecha between ?3 and ?4 order by fecha", nativeQuery = true)
    public List<ActualFlow> findAllByUsuarioFecha(Integer id, TipoActualFlow tipo, String fecha_inicio, String fecha_final);
}
