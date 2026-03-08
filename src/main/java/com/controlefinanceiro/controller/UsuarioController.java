package com.controlefinanceiro.controller;

import com.controlefinanceiro.dto.DadosAtualizacaoPerfil;
import com.controlefinanceiro.model.Usuario;
import com.controlefinanceiro.model.enums.Role;
import com.controlefinanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder; // Para criptografar a senha do novo usuário

    // DTOs internos rápidos para não expor a senha
    public record DadosCadastroUsuario(String nome, String email, String senha, Set<Role> roles) {}

    public record DadosListagemUsuario(Long id, String nome, String email, Set<Role> roles) {
        public DadosListagemUsuario(Usuario u) {
            // Agora os tipos batem perfeitamente!
            this(u.getId(), u.getNome(), u.getEmail(), u.getRoles());
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
        if (repository.findByEmail(dados.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dados.nome());
        novoUsuario.setEmail(dados.email());

        // Se o ADMIN enviou uma lista de papéis, usamos ela. Se não, é um USER comum.
        if (dados.roles() != null && !dados.roles().isEmpty()) {
            novoUsuario.setRoles(dados.roles());
        } else {
            novoUsuario.setRoles(Set.of(Role.USER));
        }

        novoUsuario.setSenhaHash(passwordEncoder.encode(dados.senha()));
        repository.save(novoUsuario);

        return ResponseEntity.ok(new DadosListagemUsuario(novoUsuario));
    }

    @PutMapping("/perfil")
    @Transactional
    public ResponseEntity<?> atualizarPerfil(@RequestBody DadosAtualizacaoPerfil dados) {
        // 1. Pega o e-mail do usuário que está logado no momento
        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = repository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. Atualiza dados básicos
        if (dados.nome() != null && !dados.nome().isBlank()) {
            usuario.setNome(dados.nome());
        }

        // Se for alterar o e-mail, idealmente teríamos que checar se já não existe outro usuário com ele
        if (dados.email() != null && !dados.email().isBlank() && !dados.email().equals(usuario.getEmail())) {
            if (repository.findByEmail(dados.email()).isPresent()) {
                return ResponseEntity.badRequest().body("Este e-mail já está em uso por outra conta.");
            }
            usuario.setEmail(dados.email());
        }

        // 3. Lógica para troca de senha
        if (dados.novaSenha() != null && !dados.novaSenha().isBlank()) {
            if (dados.senhaAtual() == null || !passwordEncoder.matches(dados.senhaAtual(), usuario.getSenhaHash())) {
                return ResponseEntity.badRequest().body("A senha atual informada está incorreta.");
            }
            usuario.setSenhaHash(passwordEncoder.encode(dados.novaSenha()));
        }

        repository.save(usuario);
        return ResponseEntity.ok("Perfil atualizado com sucesso!");
    }

    // Método extra para o Vue.js buscar os dados atuais na hora de abrir a tela
    @GetMapping("/perfil")
    public ResponseEntity<?> buscarMeuPerfil() {
        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = repository.findByEmail(emailLogado).orElseThrow();

        // Retornamos apenas o básico, NUNCA a senha!
        return ResponseEntity.ok(new ResumoPerfil(usuario.getNome(), usuario.getEmail()));
    }

    // DTO interno rápido para o Get
    public record ResumoPerfil(String nome, String email) {
    }
}