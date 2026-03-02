package com.controlefinanceiro.dto;

import java.time.LocalDate;

public record DadosAtualizacaoParcela(
        Boolean pago,
        LocalDate dataPagamentoEfetivo
) {
}