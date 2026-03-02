package com.controlefinanceiro.dto;

import com.controlefinanceiro.model.Transacao;
import com.controlefinanceiro.model.enums.Modalidade;
import com.controlefinanceiro.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosDetalhamentoTransacao(
        Long id,
        TipoTransacao tipoTransacao,
        String titulo,
        BigDecimal valorTotal,
        Modalidade modalidade,
        LocalDate dataRegistro
) {
    // Construtor inteligente que converte a Entidade no nosso DTO
    public DadosDetalhamentoTransacao(Transacao transacao) {
        this(transacao.getId(), transacao.getTipoTransacao(), transacao.getTitulo(),
                transacao.getValorTotal(), transacao.getModalidade(), transacao.getDataRegistro());
    }
}