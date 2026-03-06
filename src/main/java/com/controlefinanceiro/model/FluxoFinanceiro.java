package com.controlefinanceiro.model;

import com.controlefinanceiro.model.enums.StatusParcela;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fluxo_financeiro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FluxoFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento de volta para a Transação "Pai"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transacao_id", nullable = false)
    private Transacao transacao;

    @Column(name = "numero_parcela", nullable = false)
    private Integer numeroParcela = 1;

    @Column(name = "valor_parcela", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorParcela;

    @Column(name = "data_competencia", nullable = false)
    private LocalDate dataCompetencia;

    @Column(name = "data_pagamento_efetivo")
    private LocalDate dataPagamentoEfetivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusParcela status = StatusParcela.PENDENTE;

    @Column(name = "valor_pago")
    private BigDecimal valorPago;
}
