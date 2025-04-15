package com.example.tryJwt.demo.Modelo;

import java.util.Date;

public interface Flow {
    Integer getId();
    void setId(Integer id);
    Double getMonto();
    void setMonto(Double monto);
    String getTipo();
    void setTipo(String tipo);
    String getDescripcion();
    void setDescripcion(String descripcion);
    Date getFecha();
    void setFecha(Date fecha);
    Users getUsuario();
    void setUsuario(Users user);
    void setMoneda(String moneda);
    String getMoneda();
}
