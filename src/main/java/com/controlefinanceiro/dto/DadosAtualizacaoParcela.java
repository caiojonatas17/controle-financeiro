package com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosAtualizacaoParcela(
        Boolean pago,
        BigDecimal valorPago,
        LocalDate dataPagamentoEfetivo
) {
}