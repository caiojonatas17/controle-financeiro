package com.controlefinanceiro.controller;

import com.controlefinanceiro.model.Usuario;
import com.controlefinanceiro.model.enums.Role;
import com.controlefinanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder; // Para criptografar a senha do novo usuário

    // DTOs internos rápidos para não expor a senha
    public record DadosCadastroUsuario(String nome, String email, String senha, Role role) {}
    public record DadosListagemUsuario(Long id, String nome, String email, Role role) {
        public DadosListagemUsuario(Usuario u) {
            this(u.getId(), u.getNome(), u.getEmail(), u.getRole());
        }
    }

    @GetMapping
    public ResponseEntity<List<DadosListagemUsuario>> listar() {
        // Busca todos e converte para o DTO sem a senha
        var lista = repository.findAll().stream().map(DadosListagemUsuario::new).toList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<DadosListagemUsuario> cadastrar(@RequestBody DadosCadastroUsuario dados) {
        // Verifica se o email já existe
        if (repository.findByEmail(dados.email()).isPresent()) {
            return ResponseEntity.badRequest().build(); // Email já em uso
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dados.nome());
        novoUsuario.setEmail(dados.email());
        novoUsuario.setRole(dados.role() != null ? dados.role() : Role.USER);

        // Criptografa a senha antes de salvar!
        novoUsuario.setSenhaHash(passwordEncoder.encode(dados.senha()));

        repository.save(novoUsuario);

        return ResponseEntity.ok(new DadosListagemUsuario(novoUsuario));
    }
}