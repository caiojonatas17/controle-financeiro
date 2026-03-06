package com.controlefinanceiro.repository;

import com.controlefinanceiro.model.FluxoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FluxoFinanceiroRepository extends JpaRepository<FluxoFinanceiro, Long> {

    List<FluxoFinanceiro> findByTransacaoId(Long transacaoId);

    @Query("SELECT f FROM FluxoFinanceiro f WHERE f.transacao.usuario.id = :usuarioId " +
            "AND f.dataCompetencia BETWEEN :dataInicio AND :dataFim")
    List<FluxoFinanceiro> findFluxosPorUsuarioEPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    // ADICIONE ESTE NOVO MÉTODO:
    // Garante que a parcela pertence a uma transação do dono do Token
    @Query("SELECT f FROM FluxoFinanceiro f WHERE f.id = :idParcela AND f.transacao.usuario.email = :email")
    Optional<FluxoFinanceiro> buscarPorIdEEmailUsuario(@Param("idParcela") Long idParcela, @Param("email") String email);

    @Query("""
        SELECT f FROM FluxoFinanceiro f 
        WHERE f.transacao.usuario.email = :email 
        AND f.transacao.arquivado = false 
        AND f.status = 'PENDENTE' 
        AND f.dataCompetencia <= :hoje 
        ORDER BY f.dataCompetencia ASC
    """)
    List<FluxoFinanceiro> buscarPendentesAteData(String email, LocalDate hoje);
}