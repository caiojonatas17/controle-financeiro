package com.controlefinanceiro.repository;

import com.controlefinanceiro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring Boot é inteligente o suficiente para ler o nome deste método
    // e criar o comando SQL: SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByDonoDaConta(Usuario dono);

    boolean existsByEmail(String email);
}