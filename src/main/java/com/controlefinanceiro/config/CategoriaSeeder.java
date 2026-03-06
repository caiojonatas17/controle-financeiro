package com.controlefinanceiro.config;

import com.controlefinanceiro.model.Categoria;
import com.controlefinanceiro.model.enums.TipoTransacao;
import com.controlefinanceiro.repository.CategoriaRepository;
import com.controlefinanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CategoriaSeeder implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        // Se já existirem categorias no banco, não fazemos nada para não duplicar
        if (categoriaRepository.count() > 0) {
            return;
        }

        // Busca o usuário de ID 1 (O seu usuário principal que já existe no banco)
        usuarioRepository.findById(1L).ifPresent(usuario -> {

            // Altere a lista para incluir o TipoTransacao em cada uma:
            List<Categoria> categoriasPadrao = List.of(
                    // Categorias de GASTO
                    new Categoria(null, "Alimentação", "orange", "restaurant", TipoTransacao.GASTO, new BigDecimal("800.00"), usuario),
                    new Categoria(null, "Transporte", "blue", "directions_car", TipoTransacao.GASTO, new BigDecimal("400.00"), usuario),
                    new Categoria(null, "Moradia", "purple", "home", TipoTransacao.GASTO, new BigDecimal("1500.00"), usuario),
                    new Categoria(null, "Lazer", "pink", "sports_esports", TipoTransacao.GASTO, new BigDecimal("300.00"), usuario),
                    new Categoria(null, "Saúde", "red", "favorite", TipoTransacao.GASTO, new BigDecimal("200.00"), usuario),

                    // Categorias de RECEBIVEL
                    new Categoria(null, "Salário", "positive", "attach_money", TipoTransacao.RECEBIVEL, BigDecimal.ZERO, usuario),
                    new Categoria(null, "Freelance", "teal", "computer", TipoTransacao.RECEBIVEL, BigDecimal.ZERO, usuario)
            );

            categoriaRepository.saveAll(categoriasPadrao);
            System.out.println("✅ Categorias padrão injetadas com sucesso para o usuário: " + usuario.getEmail());
        });
    }
}