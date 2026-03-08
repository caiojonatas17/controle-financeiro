package com.controlefinanceiro.controller;

import com.controlefinanceiro.dto.DadosCategoria;
import com.controlefinanceiro.dto.DadosOrcamentoCategoria;
import com.controlefinanceiro.model.enums.TipoTransacao;
import com.controlefinanceiro.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

//    @GetMapping
//    public ResponseEntity<List<DadosCategoria>> listar() {
//        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
//        return ResponseEntity.ok(service.listar(emailLogado));
//    }

    @GetMapping
    public ResponseEntity<List<DadosCategoria>> listar(@RequestParam(required = false) TipoTransacao tipo) {
        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(categoriaService.listar(emailLogado, tipo));
    }

    @PostMapping
    public ResponseEntity<DadosCategoria> criar(@RequestBody DadosCategoria dados) {
        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        var categoriaCriada = categoriaService.criar(dados, emailLogado);
        return ResponseEntity.ok(categoriaCriada);
    }

    @PatchMapping("/{id}/orcamento")
    public ResponseEntity<?> atualizarOrcamento(
            @PathVariable Long id,
            @RequestBody DadosOrcamentoCategoria dados) {

        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        categoriaService.atualizarOrcamento(id, dados.orcamento(), emailLogado);

        return ResponseEntity.ok().build();
    }
}