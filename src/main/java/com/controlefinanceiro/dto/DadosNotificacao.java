package com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record DadosNotificacao(
        Long fluxoId,
        String titulo,
        BigDecimal valor,
        LocalDate dataVencimento,
        long diasAtraso,
        String tipoTransacao
) {
    // Construtor personalizado para calcular os dias de atraso automaticamente
    public DadosNotificacao(Long fluxoId, String titulo, BigDecimal valor, LocalDate dataVencimento, String tipoTransacao) {
        this(
                fluxoId,
                titulo,
                valor,
                dataVencimento,
                ChronoUnit.DAYS.between(dataVencimento, LocalDate.now()), // Calcula a diferença em dias
                tipoTransacao
        );
    }
}