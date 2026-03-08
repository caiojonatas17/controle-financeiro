package com.controlefinanceiro.controller;

import com.controlefinanceiro.dto.DadosAutenticacao;
import com.controlefinanceiro.dto.DadosRedefinicaoSenha;
import com.controlefinanceiro.dto.DadosSolicitacaoCodigo;
import com.controlefinanceiro.dto.DadosTokenJWT;
import com.controlefinanceiro.model.Usuario;
import com.controlefinanceiro.repository.UsuarioRepository;
import com.controlefinanceiro.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        var usuarioLogado = (Usuario) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken(usuarioLogado);

        // Devolve o token, a role e o nome
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT, usuarioLogado.getRoles(), usuarioLogado.getNome()));
    }

    @PostMapping("/esqueci-senha")
    @Transactional
    public ResponseEntity<?> solicitarCodigo(@RequestBody DadosSolicitacaoCodigo dados) {
        var usuarioOptional = usuarioRepository.findByEmail(dados.email());

        if (usuarioOptional.isEmpty()) {
            // Por segurança, não dizemos se o e-mail existe ou não, apenas retornamos OK
            return ResponseEntity.ok("Se o e-mail existir, um código será enviado.");
        }

        var usuario = usuarioOptional.get();

        // Gera um código de 6 dígitos
        String codigo = String.format("%06d", new Random().nextInt(999999));

        // Salva o código e a validade (15 minutos)
        usuario.setCodigoRecuperacao(codigo);
        usuario.setValidadeCodigo(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        // AQUI ESTÁ O NOSSO "E-MAIL" TEMPORÁRIO!
        System.out.println("=================================================");
        System.out.println("CÓDIGO DE RECUPERAÇÃO PARA " + usuario.getEmail() + ": " + codigo);
        System.out.println("=================================================");

        return ResponseEntity.ok("Código gerado com sucesso.");
    }

    @PostMapping("/redefinir-senha")
    @Transactional
    public ResponseEntity<?> redefinirSenha(@RequestBody DadosRedefinicaoSenha dados) {
        var usuarioOptional = usuarioRepository.findByEmail(dados.email());

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado.");
        }

        var usuario = usuarioOptional.get();

        // Valida se o código está correto e se não expirou
        if (usuario.getCodigoRecuperacao() == null ||
                !usuario.getCodigoRecuperacao().equals(dados.codigo()) ||
                usuario.getValidadeCodigo().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Código inválido ou expirado.");
        }

        // Tudo certo! Criptografa a nova senha e salva
        // Certifique-se de que tem o PasswordEncoder injetado no seu AuthController!
        usuario.setSenhaHash(passwordEncoder.encode(dados.novaSenha()));

        // Limpa o código para não ser usado de novo
        usuario.setCodigoRecuperacao(null);
        usuario.setValidadeCodigo(null);

        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
}