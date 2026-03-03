package com.controlefinanceiro.dto;

import com.controlefinanceiro.model.Categoria;
import com.controlefinanceiro.model.enums.TipoTransacao;

public record DadosCategoria(
        Long id,
        String nome,
        String cor,
        String icone,
        TipoTransacao tipoTransacao
) {
    public DadosCategoria(Categoria categoria) {
        this(categoria.getId(), categoria.getNome(), categoria.getCor(), categoria.getIcone(), categoria.getTipoTransacao());
    }
}