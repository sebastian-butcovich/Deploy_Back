package com.example.tryJwt.demo.Modelo;

import com.example.tryJwt.demo.Enums.TipoActualFlow;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ActualFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo", nullable = false)
    private TipoActualFlow tipo;

    @Column(name = "descripción")
    private String descripcion;

    @Column (name = "monto", nullable = false)
    private Double monto;

    @Column(name = "fecha",  nullable = false)
    private Date fecha;

    @JoinColumn(name = "id_autor", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Users usuario;

    @Transient
    private String moneda;

}
