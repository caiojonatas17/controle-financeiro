package com.controlefinanceiro.repository;

import com.controlefinanceiro.model.Categoria;
import com.controlefinanceiro.model.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Vai buscar todas as categorias de um utilizador específico
    List<Categoria> findByUsuarioEmail(String email);

    List<Categoria> findByUsuarioEmailAndTipoTransacao(String email, TipoTransacao tipoTransacao);

}