package com.controlefinanceiro.dto;

// DTO aninhado para ler exatamente o formato JSON da Hotmart
public record HotmartWebhookDTO(String event, DataDTO data) {
    public record DataDTO(BuyerDTO buyer) {}
    public record BuyerDTO(String name, String email) {}
}