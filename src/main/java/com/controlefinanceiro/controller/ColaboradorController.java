package com.controlefinanceiro.controller;

import com.controlefinanceiro.model.Usuario;
import com.controlefinanceiro.model.enums.Role;
import com.controlefinanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/colaboradores")
@RequiredArgsConstructor
public class ColaboradorController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public record DadosNovoColaborador(String nome, String email, String senha, Set<Role> papeis) {}

    @PostMapping
    @Transactional
    public ResponseEntity<?> adicionarColaborador(@RequestBody DadosNovoColaborador dados) {
        // 1. Quem está a convidar? (O Dono)
        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario donoDaConta = usuarioRepository.findByEmail(emailLogado).orElseThrow();

        // 2. Verifica se o e-mail do colaborador já existe no sistema
        if (usuarioRepository.findByEmail(dados.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Este e-mail já está registado no sistema.");
        }

        // 3. Cria o Colaborador
        Usuario colaborador = new Usuario();
        colaborador.setNome(dados.nome());
        colaborador.setEmail(dados.email());
        colaborador.setSenhaHash(passwordEncoder.encode(dados.senha()));
        colaborador.setDonoDaConta(donoDaConta); // <-- A MÁGICA DO VÍNCULO AQUI!

        // 4. Atribui os múltiplos papéis escolhidos
        colaborador.setRoles(dados.papeis());

        usuarioRepository.save(colaborador);

        return ResponseEntity.ok("Colaborador adicionado com sucesso!");
    }

    @GetMapping
    public ResponseEntity<?> listarEquipe() {
        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario donoDaConta = usuarioRepository.findByEmail(emailLogado).orElseThrow();

        // Busca todos os utilizadores cujo 'donoDaConta' seja o utilizador atual
        var equipe = usuarioRepository.findByDonoDaConta(donoDaConta).stream()
                .map(c -> new DadosListagemEquipe(c.getId(), c.getNome(), c.getEmail(), c.getRoles()))
                .toList();

        return ResponseEntity.ok(equipe);
    }

    // DTO rápido para a listagem (adicione no final do arquivo ou no seu pacote de DTOs)
    public record DadosListagemEquipe(Long id, String nome, String email, Set<Role> papeis) {}
}