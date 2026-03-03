package com.controlefinanceiro.dto;

import com.controlefinanceiro.model.enums.Role;

public record DadosTokenJWT(String token, Role role, String nome) {
}