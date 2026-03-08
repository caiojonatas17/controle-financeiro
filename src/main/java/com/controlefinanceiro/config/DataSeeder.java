package com.controlefinanceiro.config; // Ajuste para o seu pacote

import com.controlefinanceiro.model.Usuario;
import com.controlefinanceiro.model.enums.Role;
import com.controlefinanceiro.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Se não houver nenhum usuário no banco, cria o primeiro
            if (repository.count() == 0) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador do Sistema");
                admin.setEmail("admin@admin.com");

                // Criptografa a senha padrão (use setSenha ou setSenhaHash dependendo de como está na sua entidade)
                admin.setSenhaHash(passwordEncoder.encode("123456"));

                // Vamos dar logo o poder total para não termos problemas de bloqueio
                admin.setRoles(Set.of(Role.ADMIN, Role.USER));

                repository.save(admin);

                System.out.println("=======================================================");
                System.out.println("✅ USUÁRIO PADRÃO CRIADO COM SUCESSO!");
                System.out.println("Login: admin@admin.com");
                System.out.println("Senha: 123456");
                System.out.println("=======================================================");
            }
        };
    }
}