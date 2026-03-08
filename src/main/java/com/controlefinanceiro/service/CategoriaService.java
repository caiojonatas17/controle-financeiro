package com.controlefinanceiro.service;

import com.controlefinanceiro.dto.DadosCategoria;
import com.controlefinanceiro.model.Categoria;
import com.controlefinanceiro.model.Usuario;
import com.controlefinanceiro.model.enums.TipoTransacao;
import com.controlefinanceiro.repository.CategoriaRepository;
import com.controlefinanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<DadosCategoria> listar(String emailUsuario) {
        return categoriaRepository.findByUsuarioEmail(emailUsuario).stream()
                .map(DadosCategoria::new)
                .toList();
    }

    public List<DadosCategoria> listar(String emailUsuario, TipoTransacao tipo) {
        if (tipo == null) {
            return categoriaRepository.findByUsuarioEmail(emailUsuario).stream().map(DadosCategoria::new).toList();
        }
        return categoriaRepository.findByUsuarioEmailAndTipoTransacao(emailUsuario, tipo)
                .stream().map(DadosCategoria::new).toList();
    }

    public DadosCategoria criar(DadosCategoria dados, String emailUsuario) {
        var usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Categoria categoria = new Categoria();
        categoria.setNome(dados.nome());
        categoria.setCor(dados.cor());
        categoria.setIcone(dados.icone());
        categoria.setUsuario(usuario);
        categoria.setTipoTransacao(dados.tipoTransacao());

        categoriaRepository.save(categoria);

        return new DadosCategoria(categoria);
    }

    @Transactional
    public void atualizarOrcamento(Long idCategoria, BigDecimal novoOrcamento, String emailUsuario) {
        // 1. Busca o usuário logado
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. Busca a categoria
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        // 3. Validação de segurança: a categoria pertence a este usuário?
        if (!categoria.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: esta categoria não pertence ao usuário logado.");
        }

        // 4. Atualiza o valor
        categoria.setOrcamento(novoOrcamento);

        // O save() é opcional aqui por causa do @Transactional, mas é uma boa prática deixar explícito
        categoriaRepository.save(categoria);
    }
}