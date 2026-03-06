package com.controlefinanceiro.repository;

import com.controlefinanceiro.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    @Query("SELECT t FROM Transacao t WHERE t.tipoTransacao = 'GASTO' " +
            "AND (t.usuario.email = :email) " +
            "AND (:contaId IS NULL OR t.conta.id = :contaId) " +
            "AND (:categoriaId IS NULL OR t.categoria.id = :categoriaId) " +
            "AND (:modalidade IS NULL OR t.modalidade = :modalidade) " +
            "AND (CAST(:dataInicio AS date) IS NULL OR t.dataRegistro >= :dataInicio) " +
            "AND (CAST(:dataFim AS date) IS NULL OR t.dataRegistro <= :dataFim)")
    List<Transacao> pesquisarGastos(
            @Param("email") String email,
            @Param("contaId") Long contaId,
            @Param("categoriaId") Long categoriaId,
            @Param("modalidade") String modalidade,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("SELECT t FROM Transacao t WHERE t.tipoTransacao = 'RECEBIVEL' " +
            "AND (t.usuario.email = :email) " +
            "AND (:contaId IS NULL OR t.conta.id = :contaId) " +
            "AND (:categoriaId IS NULL OR t.categoria.id = :categoriaId) " +
            "AND (:modalidade IS NULL OR t.modalidade = :modalidade) " +
            "AND (CAST(:dataInicio AS date) IS NULL OR t.dataRegistro >= :dataInicio) " +
            "AND (CAST(:dataFim AS date) IS NULL OR t.dataRegistro <= :dataFim)")
    List<Transacao> pesquisarRecebivel(
            @Param("email") String email,
            @Param("contaId") Long contaId,
            @Param("categoriaId") Long categoriaId,
            @Param("modalidade") String modalidade,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );
}