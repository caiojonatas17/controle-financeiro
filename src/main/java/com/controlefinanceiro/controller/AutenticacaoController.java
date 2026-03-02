package com.controlefinanceiro.controller;

import com.controlefinanceiro.dto.DadosAutenticacao;
import com.controlefinanceiro.dto.DadosTokenJWT;
import com.controlefinanceiro.model.Usuario;
import com.controlefinanceiro.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        System.out.println("Tentando logar com o email: " + dados.email() + " e senha: " + dados.senha());
        // Empacota o email e senha que chegaram da requisição
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        // O Spring Security vai lá no banco, busca o usuário pelo e-mail e compara a senha criptografada
        var authentication = manager.authenticate(authenticationToken);

        // Se a senha estiver correta, geramos o token JWT
        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        // Devolvemos o token na resposta com o status 200 (OK)
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
}