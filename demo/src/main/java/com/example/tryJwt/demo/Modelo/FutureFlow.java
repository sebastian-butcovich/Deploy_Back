package com.example.tryJwt.demo.Modelo;

import com.example.tryJwt.demo.Enums.EstadoPD;
import com.example.tryJwt.demo.Enums.TipoFutureFlow;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
@Entity
@Table(name = "futureFlows")
@Getter
@Setter
public class FutureFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "estado", nullable = false)
    private EstadoPD estado;

    @Column(name = "tipo", nullable = false)
    private TipoFutureFlow tipo;

    @Column(name = "subtipo", nullable = false)
    private String subtipo;  // Subtipos que puede escribir el usuario (ej: gastos ordinarios, extraordinarios, entretenimiento, etc.)

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "monto", nullable = false)
    private double monto;

    @Column(name = "nombreContraparte",  nullable = false)
    private String nombreContraparte;

    @Column(name = "valorDolar", nullable = false)
    private double valorDolar;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "fechaCreacion", nullable = false)
    private Date fechaCreacion;

    @Column(name = "fechaUltimaModificacion", nullable = false)
    private Date fechaUltimaModificacion;

    @Column(name = "fechaEstimadaPago")
    private LocalDate fechaEstimadaDePago;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @Override
    public boolean equals(Object o) {
        if(o instanceof FutureFlow futureFlow) {
            return this.getNombreContraparte().equals(futureFlow.getNombreContraparte()) && this.getFecha()==futureFlow.getFecha()
                    && futureFlow.getFecha().getMonth() == this.getFecha().getMonth() && futureFlow.getFecha().getYear() == this.getFecha().getYear()
                    && futureFlow.getMonto() == this.getMonto();
        }else {return false;}
    }

    public static boolean isValid(FutureFlow ff) {
        return !(ff.getTipo() == null || ff.getSubtipo() == null || ff.getSubtipo().isEmpty() ||
                ff.getMonto() == 0.0 || ff.getNombreContraparte() == null || ff.getNombreContraparte().isEmpty() ||
                ff.getValorDolar() == 0.0 || ff.getFecha() == null || ff.getFechaCreacion() == null || ff.getFechaUltimaModificacion() == null);
    }
}
