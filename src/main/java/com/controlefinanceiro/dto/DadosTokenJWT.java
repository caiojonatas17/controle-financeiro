package com.controlefinanceiro.dto;

import com.controlefinanceiro.model.enums.Role;

import java.util.Set;

public record DadosTokenJWT(String token, Set<Role> roles, String nome) {
}