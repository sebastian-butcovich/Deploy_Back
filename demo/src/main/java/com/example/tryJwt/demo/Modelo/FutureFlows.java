package com.example.tryJwt.demo.Modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Entity
@Table(name = "debts")
@Getter
@Setter
public class FutureFlows {
    private enum EstadoPD{
        INCONCLUSO,
        PAGADO
    }
    private enum TipoFlow {
        DEUDA,
        PRESTAMO
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private EstadoPD estado;
    private TipoFlow tipo;
    private double monto;
    private String nombreDelAdeudado;
    private double valorDelDolar;
    private Date fecha;
    private Date fechaEstimadaDePago;
   @ManyToOne(fetch = FetchType.LAZY)
    private Users usuario;

    public Date getFechaEstimadaDePago() {
        return fechaEstimadaDePago;
    }

    public void setFechaEstimadaDePago(Date fechaEstimadaDePago) {
        this.fechaEstimadaDePago = fechaEstimadaDePago;
    }

    public EstadoPD getEstado() {
        return estado;
    }

    public TipoFlow getTipo() {
        return tipo;
    }

    public void setTipo(TipoFlow tipo) {
        this.tipo = tipo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getNombreDelAdeudado() {
        return nombreDelAdeudado;
    }

    public void setNombreDelAdeudado(String nombre) {
        this.nombreDelAdeudado = nombre;
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
        if(o instanceof FutureFlows debts) {
            return this.getNombreDelAdeudado().equals(debts.getNombreDelAdeudado()) && this.getFecha().getDate()==debts.getFecha().getDate()
                    && debts.getFecha().getMonth() == this.getFecha().getMonth() && debts.getFecha().getYear() == this.getFecha().getYear()
                    && debts.getMonto() == this.getMonto();
        }else {return false;}
    }
}
