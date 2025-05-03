package com.example.tryJwt.demo.Modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "debts")
public class Debts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private boolean estado;
    private double monto;
    private String nombre;
    private double valorDelDolar;
    private Date fecha;
   @ManyToOne(fetch = FetchType.LAZY)
    private Users usuario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getValorDelDolar() {
        return valorDelDolar;
    }

    public void setValorDelDolar(double valorDelDolar) {
        this.valorDelDolar = valorDelDolar;
    }
    @JsonBackReference
    public Users getUsuario() {
        return usuario;
    }

    public void setUsuario(Users usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof Debts debts) {
            return this.getNombre().equals(debts.getNombre()) && this.getFecha().getDate()==debts.getFecha().getDate()
                    && debts.getFecha().getMonth() == this.getFecha().getMonth() && debts.getFecha().getYear() == this.getFecha().getYear()
                    && debts.getMonto() == this.getMonto();
        }else {return false;}
    }
}
