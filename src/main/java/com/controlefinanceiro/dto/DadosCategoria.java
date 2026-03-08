package com.controlefinanceiro.dto;

import com.controlefinanceiro.model.Categoria;
import com.controlefinanceiro.model.enums.TipoTransacao;

import java.math.BigDecimal;

public record DadosCategoria(
        Long id,
        String nome,
        String cor,
        String icone,
        TipoTransacao tipoTransacao,
        BigDecimal orcamento
) {
    public DadosCategoria(Categoria categoria) {
        this(categoria.getId(), categoria.getNome(), categoria.getCor(), categoria.getIcone(),
                categoria.getTipoTransacao(), categoria.getOrcamento());
    }
}