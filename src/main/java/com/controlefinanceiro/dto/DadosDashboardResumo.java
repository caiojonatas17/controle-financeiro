package com.controlefinanceiro.dto;

import java.math.BigDecimal;

public record DadosDashboardResumo(
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldo
) {
}