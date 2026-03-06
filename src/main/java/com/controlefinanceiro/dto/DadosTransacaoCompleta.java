package com.controlefinanceiro.dto;

import com.controlefinanceiro.model.Transacao;
import com.controlefinanceiro.model.enums.Modalidade;
import com.controlefinanceiro.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DadosTransacaoCompleta(
        Long id,
        TipoTransacao tipoTransacao,
        String titulo,
        String descricao,
        String agente,
        BigDecimal valorTotal,
        Modalidade modalidade,
        LocalDate dataRegistro,
        List<DadosParcela> parcelas
) {
    public DadosTransacaoCompleta(Transacao transacao) {
        this(transacao.getId(), transacao.getTipoTransacao(), transacao.getTitulo(),
                transacao.getDescricao(), transacao.getConta() != null ? transacao.getConta().getNome() : "Sem Conta", transacao.getValorTotal(),
                transacao.getModalidade(), transacao.getDataRegistro(),
                // Aqui a mágica acontece: convertemos a lista do banco para a lista do DTO
                transacao.getParcelas().stream().map(DadosParcela::new).toList()
        );
    }
}