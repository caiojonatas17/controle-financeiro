package com.controlefinanceiro.service;

import com.controlefinanceiro.dto.DadosCategoria;
import com.controlefinanceiro.model.Categoria;
import com.controlefinanceiro.model.enums.TipoTransacao;
import com.controlefinanceiro.repository.CategoriaRepository;
import com.controlefinanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;
    private final UsuarioRepository usuarioRepository;

    public List<DadosCategoria> listar(String emailUsuario) {
        return repository.findByUsuarioEmail(emailUsuario).stream()
                .map(DadosCategoria::new)
                .toList();
    }

    public List<DadosCategoria> listar(String emailUsuario, TipoTransacao tipo) {
        if (tipo == null) {
            return repository.findByUsuarioEmail(emailUsuario).stream().map(DadosCategoria::new).toList();
        }
        return repository.findByUsuarioEmailAndTipoTransacao(emailUsuario, tipo)
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

        repository.save(categoria);

        return new DadosCategoria(categoria);
    }
}