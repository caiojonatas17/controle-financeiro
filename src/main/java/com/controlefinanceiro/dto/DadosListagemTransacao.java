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
        String descricao,
        String agente,
        BigDecimal valorTotal,
        Modalidade modalidade,
        LocalDate dataRegistro,
        Boolean arquivado,
        DadosCategoria categoria
) {


    public DadosListagemTransacao(Transacao transacao) {
        this(transacao.getId(), transacao.getTipoTransacao(), transacao.getTitulo(),
                transacao.getDescricao(), transacao.getConta() != null ? transacao.getConta().getNome() : "Sem Conta", transacao.getValorTotal(),
                transacao.getModalidade(), transacao.getDataRegistro(), transacao.getArquivado(),
                transacao.getCategoria() != null ? new DadosCategoria(transacao.getCategoria()) : null);
    }
}