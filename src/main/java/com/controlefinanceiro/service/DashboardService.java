package com.controlefinanceiro.service;

import com.controlefinanceiro.dto.DadosCategoriaSoma;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // Agora temos DOIS mapas
        Map<String, DadosCategoriaSoma> mapaGastos = new HashMap<>();
        Map<String, DadosCategoriaSoma> mapaReceitas = new HashMap<>();

        // Mapa para ir somando os gastos de cada categoria
        Map<String, DadosCategoriaSoma> mapaCategorias = new HashMap<>();
        // 5. Separa o que é Gasto do que é Recebimento com filtros de segurança
        for (FluxoFinanceiro fluxo : fluxosDoMes) {
            // FILTRO 1: Ignorar se a transação estiver arquivada
            if (fluxo.getTransacao().getArquivado()) continue;

            var categoria = fluxo.getTransacao().getCategoria();

            // FILTRO 2: Regra para Recebíveis (Só entram se estiverem pagos/recebidos)
            if (fluxo.getTransacao().getTipoTransacao() == TipoTransacao.RECEBIVEL) {
                if (fluxo.getStatus() == StatusParcela.PAGO) {
                    totalReceitas = totalReceitas.add(fluxo.getValorParcela());
                    // Lógica para somar Receitas por Categoria
                    if (categoria != null) {
                        String nomeCat = categoria.getNome();
                        BigDecimal somaAtual = mapaReceitas.containsKey(nomeCat) ? mapaReceitas.get(nomeCat).total() : BigDecimal.ZERO;
                        mapaReceitas.put(nomeCat, new DadosCategoriaSoma(nomeCat, categoria.getCor(), somaAtual.add(fluxo.getValorParcela())));
                    }
                }
            }
            // FILTRO 3: Regra para Gastos
            else if (fluxo.getTransacao().getTipoTransacao() == TipoTransacao.GASTO) {
                totalDespesas = totalDespesas.add(fluxo.getValorParcela());
                // Lógica para somar Gastos por Categoria
                if (categoria != null) {
                    String nomeCat = categoria.getNome();
                    BigDecimal somaAtual = mapaGastos.containsKey(nomeCat) ? mapaGastos.get(nomeCat).total() : BigDecimal.ZERO;
                    mapaGastos.put(nomeCat, new DadosCategoriaSoma(nomeCat, categoria.getCor(), somaAtual.add(fluxo.getValorParcela())));
                }
            }
        }

        // 6. Calcula o saldo líquido real
        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        // Converte o mapa para a lista que o frontend espera
        List<DadosCategoriaSoma> gastosPorCategoria = mapaCategorias.values().stream().toList();

        return new DadosDashboardResumo(
                totalReceitas,
                totalDespesas,
                saldo,
                mapaGastos.values().stream().toList(),
                mapaReceitas.values().stream().toList() // Passamos a segunda lista
        );
    }
}