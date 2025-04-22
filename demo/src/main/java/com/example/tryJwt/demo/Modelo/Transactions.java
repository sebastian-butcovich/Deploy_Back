package com.example.tryJwt.demo.Modelo;

import java.util.Date;

public interface Transactions {
    public void setId(Integer id);
    public Integer getId();
    public void setMonto(Double monto);
    public Double getMonto();
    public void setTipo(String type);
    public String getTipo();
    public void setDescripcion(String descripcion);
    public String getDescripcion();
    public void setFecha(Date fecha);
    public Date getFecha();
    public void setMoneda(String moneda);
    public String getMoneda();
}
