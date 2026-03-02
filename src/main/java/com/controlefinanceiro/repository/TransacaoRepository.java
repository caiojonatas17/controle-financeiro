package com.controlefinanceiro.repository;

import com.controlefinanceiro.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    // Busca todas as transações (gastos ou recebíveis) de um usuário logado
    List<Transacao> findByUsuarioId(Long usuarioId);

    // Busca transações de um usuário separando o que está arquivado ou não
    List<Transacao> findByUsuarioIdAndArquivado(Long usuarioId, Boolean arquivado);

    List<Transacao> findByUsuarioEmailAndArquivadoTrue(String email);

    // Retorna a lista simples para a tela inicial
    List<Transacao> findByUsuarioEmailAndArquivadoFalse(String email);

    // Retorna a transação completa (com JOIN nas parcelas) validando o dono
    @Query("SELECT t FROM Transacao t LEFT JOIN FETCH t.parcelas WHERE t.id = :id AND t.usuario.email = :email")
    Optional<Transacao> buscarPorIdEEmailComParcelas(@Param("id") Long id, @Param("email") String email);
}