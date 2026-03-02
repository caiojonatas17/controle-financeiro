package com.controlefinanceiro;
import com.controlefinanceiro.model.Usuario;
import com.controlefinanceiro.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ControlefinanceiroApplication { // Nome da sua classe principal

    public static void main(String[] args) {
        SpringApplication.run(ControlefinanceiroApplication.class, args);
    }

    // ADICIONE ESTE BLOCO ABAIXO DO MAIN:
//    @Bean
//    public CommandLineRunner resetarSenhaAdmin(UsuarioRepository repository, PasswordEncoder encoder) {
//        return args -> {
//            var usuario = repository.findByEmail("admin@admin.com");
//            if (usuario.isPresent()) {
//                Usuario u = usuario.get();
//                u.setSenhaHash(encoder.encode("123456")); // O Java faz o hash perfeitamente
//                repository.save(u);
//                System.out.println("✅ SENHA DO ADMIN ATUALIZADA COM SUCESSO PELO SPRING BOOT!");
//            }
//        };
//    }
}