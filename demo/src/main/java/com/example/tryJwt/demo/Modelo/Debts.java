package com.example.tryJwt.demo.Modelo;

import jakarta.persistence.*;

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
   @ManyToOne(fetch = FetchType.LAZY)
    private Users usuario;
}
