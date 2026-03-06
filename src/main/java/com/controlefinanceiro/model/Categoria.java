package com.controlefinanceiro.model;

import com.controlefinanceiro.model.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cor;    // Ex: "positive", "negative", "blue", "orange"
    private String icone;  // Ex: "restaurant", "directions_car", "home"

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipoTransacao; // Diz se essa categoria é de GASTO ou RECEBIVEL

    @Column(name = "orcamento")
    private BigDecimal orcamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


}