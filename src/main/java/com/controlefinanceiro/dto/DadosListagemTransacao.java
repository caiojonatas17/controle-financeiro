package com.controlefinanceiro.dto;

import com.controlefinanceiro.model.Transacao;
import com.controlefinanceiro.model.enums.Modalidade;
import com.controlefinanceiro.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosListagemTransacao(
        Long id,
        TipoTransacao tipoTransacao,
        String titulo,
        String agente,
        BigDecimal valorTotal,
        Modalidade modalidade,
        LocalDate dataRegistro,
        Boolean arquivado
) {
    public DadosListagemTransacao(Transacao transacao) {
        this(transacao.getId(), transacao.getTipoTransacao(), transacao.getTitulo(),
                transacao.getAgente(), transacao.getValorTotal(), transacao.getModalidade(),
                transacao.getDataRegistro(), transacao.getArquivado());
    }
}