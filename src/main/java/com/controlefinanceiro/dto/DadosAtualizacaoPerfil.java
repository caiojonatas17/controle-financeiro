package com.controlefinanceiro.dto;

public record DadosAtualizacaoPerfil(
        String nome,
        String email,
        String senhaAtual, // Necessário apenas se ele quiser trocar a senha
        String novaSenha   // A nova senha que ele deseja
) {
}