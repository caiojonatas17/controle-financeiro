package com.controlefinanceiro.repository;

import com.controlefinanceiro.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    List<Conta> findAllByUsuarioId(Long usuarioId);
}