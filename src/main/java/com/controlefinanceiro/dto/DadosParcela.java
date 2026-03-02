package com.controlefinanceiro.dto;

import com.controlefinanceiro.model.FluxoFinanceiro;
import com.controlefinanceiro.model.enums.StatusParcela;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosParcela(
        Long id,
        Integer numeroParcela,
        BigDecimal valorParcela,
        LocalDate dataCompetencia,
        LocalDate dataPagamentoEfetivo,
        StatusParcela status
) {
    public DadosParcela(FluxoFinanceiro fluxo) {
        this(fluxo.getId(), fluxo.getNumeroParcela(), fluxo.getValorParcela(),
                fluxo.getDataCompetencia(), fluxo.getDataPagamentoEfetivo(), fluxo.getStatus());
    }
}