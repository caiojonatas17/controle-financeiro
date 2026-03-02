package com.controlefinanceiro.service;

import com.controlefinanceiro.dto.DadosDashboardResumo;
import com.controlefinanceiro.model.FluxoFinanceiro;
import com.controlefinanceiro.model.enums.StatusParcela;
import com.controlefinanceiro.model.enums.TipoTransacao;
import com.controlefinanceiro.repository.FluxoFinanceiroRepository;
import com.controlefinanceiro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private FluxoFinanceiroRepository fluxoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public DadosDashboardResumo obterResumoDoMes(int ano, int mes, String emailUsuario) {

        // 1. Busca o ID do usuário através do email
        var usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. Descobre qual é o primeiro e o último dia do mês solicitado
        YearMonth anoMes = YearMonth.of(ano, mes);
        LocalDate dataInicio = anoMes.atDay(1); // Ex: 2026-02-01
        LocalDate dataFim = anoMes.atEndOfMonth(); // Ex: 2026-02-28

        // 3. Usa aquele método personalizado que criamos lá no repositório!
        List<FluxoFinanceiro> fluxosDoMes = fluxoRepository.findFluxosPorUsuarioEPeriodo(
                usuario.getId(), dataInicio, dataFim
        );

        // 4. Inicia os contadores com ZERO
        BigDecimal totalReceitas = BigDecimal.ZERO;
        BigDecimal totalDespesas = BigDecimal.ZERO;

        // 5. Separa o que é Gasto do que é Recebimento com filtros de segurança
        for (FluxoFinanceiro fluxo : fluxosDoMes) {

            // FILTRO 1: Ignorar se a transação estiver arquivada
            if (fluxo.getTransacao().getArquivado()) {
                continue;
            }

            // FILTRO 2: Regra para Recebíveis (Só entram se estiverem pagos/recebidos)
            // "Não interessa a data do pagamento, o que vale é se ela foi recebida"
            if (fluxo.getTransacao().getTipoTransacao() == TipoTransacao.RECEBIVEL) {
                if (fluxo.getStatus() == StatusParcela.PAGO) {
                    totalReceitas = totalReceitas.add(fluxo.getValorParcela());
                }
                // Se for PENDENTE, ignoramos na soma das Receitas
            }

            // FILTRO 3: Regra para Gastos
            // Aqui costumamos somar tudo (PAGO ou PENDENTE) para você saber o que deve no mês.
            // Mas se quiser que o saldo seja apenas o "dinheiro em conta", pode filtrar aqui também.
            else if (fluxo.getTransacao().getTipoTransacao() == TipoTransacao.GASTO) {
                totalDespesas = totalDespesas.add(fluxo.getValorParcela());
            }
        }

// 6. Calcula o saldo líquido real
        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        return new DadosDashboardResumo(totalReceitas, totalDespesas, saldo);
    }
}