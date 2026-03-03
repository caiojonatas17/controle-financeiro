package com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.util.List;

public record DadosDashboardResumo(
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldo,
        List<DadosCategoriaSoma> gastosPorCategoria,
        List<DadosCategoriaSoma> receitasPorCategoria
) {
}