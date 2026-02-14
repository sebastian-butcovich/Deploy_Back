package com.example.tryJwt.demo.Modelo;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "firstname",  nullable = false)
    private String firstname;

    @Column(name = "surname",  nullable = false)
    private String surname;

    @Column(name = "username",  nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "dineroActual",  nullable = false)
    private Double dineroActual;

    @Lob
    @Column(name = "photo")
    private String foto;

    @Column(name = "roles")
    private String roles;

    @Column(name = "creado", nullable = false)
    private Date creado;

    @Column(name = "ultimaModificacion",  nullable = false)
    private Date ultimaModificacion;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ActualFlow> actualFlows;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FutureFlow> futureFlows;

}

