package com.controlefinanceiro.dto;

import java.math.BigDecimal;

public record DadosCategoriaSoma(
        String nome,
        String cor,
        BigDecimal total
) {}