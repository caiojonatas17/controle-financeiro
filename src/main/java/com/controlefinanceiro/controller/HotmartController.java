package com.controlefinanceiro.controller;

import com.controlefinanceiro.dto.HotmartWebhookDTO;
import com.controlefinanceiro.model.Usuario;
import com.controlefinanceiro.model.enums.Role;
import com.controlefinanceiro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/hotmart")
public class HotmartController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/webhook")
    public ResponseEntity<String> receberVenda(
            @RequestBody HotmartWebhookDTO payload,
            @RequestHeader(value = "X-Hotmart-Hottok", required = false) String hottok) {

        // 1. Segurança: A Hotmart envia um Token de Autenticação no Header.
        // Você pegará esse token no painel da Hotmart depois. Por enquanto, comentamos.
        /*
        String meuTokenSecreto = "TOKEN_FORNECIDO_PELA_HOTMART";
        if (hottok == null || !hottok.equals(meuTokenSecreto)) {
            return ResponseEntity.status(403).body("Acesso negado. Token inválido.");
        }
        */

        // 2. Verifica se a transação foi uma COMPRA APROVADA
        if ("PURCHASE_APPROVED".equals(payload.event())) {
            String email = payload.data().buyer().email();
            String nome = payload.data().buyer().name();

            // 3. Só cria o usuário se o e-mail não existir no banco
            if (repository.findByEmail(email).isEmpty()) {
                Usuario novoCliente = new Usuario();
                novoCliente.setNome(nome);
                novoCliente.setEmail(email);

                // Gera uma senha temporária aleatória de 6 caracteres (ex: a8f7b2)
                String senhaProvisoria = UUID.randomUUID().toString().substring(0, 6);
                novoCliente.setSenhaHash(passwordEncoder.encode(senhaProvisoria));

                // Define como usuário dono da conta
                novoCliente.setRoles(Set.of(Role.USER));

                repository.save(novoCliente);

                // TODO: No futuro, substituir este print pelo disparo real de e-mail (JavaMailSender)
                System.out.println("==================================================");
                System.out.println("💰 CHING CHING! NOVA ASSINATURA CJ FINANÇAS!");
                System.out.println("Cliente: " + nome);
                System.out.println("E-mail: " + email);
                System.out.println("Senha de Acesso Gerada: " + senhaProvisoria);
                System.out.println("==================================================");
            }
        }

        // A Hotmart exige que você responda com Status 200 OK rápido, senão ela acha que deu erro e tenta de novo.
        return ResponseEntity.ok("Webhook recebido com sucesso!");
    }
}