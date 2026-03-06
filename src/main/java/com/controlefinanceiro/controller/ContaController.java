package com.controlefinanceiro.controller;

import com.controlefinanceiro.model.Conta;
import com.controlefinanceiro.model.Usuario;
import com.controlefinanceiro.repository.ContaRepository;
import com.controlefinanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaRepository repository;
    private final UsuarioRepository usuarioRepository;

    public record DadosConta(Long id, String nome) {
        public DadosConta(Conta c) { this(c.getId(), c.getNome()); }
    }

    @GetMapping
    public ResponseEntity<List<DadosConta>> listar() {
        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        var usuario = usuarioRepository.findByEmail(emailLogado).orElseThrow();

        var lista = repository.findAllByUsuarioId(usuario.getId()).stream().map(DadosConta::new).toList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<DadosConta> cadastrar(@RequestBody DadosConta dados) {
        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        var usuario = usuarioRepository.findByEmail(emailLogado).orElseThrow();

        Conta novaConta = new Conta();
        novaConta.setNome(dados.nome());
        novaConta.setUsuario(usuario);

        repository.save(novaConta);
        return ResponseEntity.ok(new DadosConta(novaConta));
    }
}