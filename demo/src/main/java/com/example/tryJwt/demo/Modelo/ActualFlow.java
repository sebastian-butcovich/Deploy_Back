package com.example.tryJwt.demo.Modelo;

import com.example.tryJwt.demo.Enums.TipoActualFlow;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ActualFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo", nullable = false)
    private TipoActualFlow tipo;

    @Column(name = "subtipo", nullable = false)
    private String subtipo;

    @Column(name = "descripción")
    private String descripcion;

    @Column (name = "monto", nullable = false)
    private Double monto;

    @Column(name = "fecha",  nullable = false)
    private Date fecha;

    @Column(name = "fechaCreacion", nullable = false)
    private Date fechaCreacion;

    @Column(name = "fechaUltimaModificacion", nullable = false)
    private Date fechaUltimaModificacion;

    @JoinColumn(name = "id_autor", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @Transient
    private String moneda;

}
