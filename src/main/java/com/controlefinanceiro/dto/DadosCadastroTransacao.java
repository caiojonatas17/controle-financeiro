package com.controlefinanceiro.dto;

import com.controlefinanceiro.model.enums.Modalidade;
import com.controlefinanceiro.model.enums.TipoTransacao;
import com.controlefinanceiro.model.enums.Periodicidade; // <-- Novo Import

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosCadastroTransacao(
        TipoTransacao tipoTransacao,
        String titulo,
        String descricao,
        String agente,
        BigDecimal valorTotal,
        Modalidade modalidade,
        LocalDate dataRegistro,
        Integer quantidadeParcelas,
        Periodicidade periodicidade // <-- Novo Campo
) {
}