package com.controlefinanceiro.service;

import com.controlefinanceiro.dto.DadosAtualizacaoParcela;
import com.controlefinanceiro.dto.DadosCadastroTransacao;
import com.controlefinanceiro.dto.DadosListagemTransacao;
import com.controlefinanceiro.model.FluxoFinanceiro;
import com.controlefinanceiro.model.Transacao;
import com.controlefinanceiro.model.Usuario;
import com.controlefinanceiro.model.enums.Modalidade;
import com.controlefinanceiro.model.enums.Periodicidade;
import com.controlefinanceiro.model.enums.StatusParcela;
import com.controlefinanceiro.repository.FluxoFinanceiroRepository;
import com.controlefinanceiro.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private FluxoFinanceiroRepository fluxoRepository;

    // O @Transactional garante que se der erro na parcela 5, ele desfaz tudo e não salva nada pela metade no banco
    @Transactional
    public Transacao registrarTransacao(DadosCadastroTransacao dados, Usuario usuarioLogado) {

        var transacao = Transacao.builder()
                .usuario(usuarioLogado)
                .tipoTransacao(dados.tipoTransacao())
                .titulo(dados.titulo())
                .descricao(dados.descricao())
                .agente(dados.agente())
                .valorTotal(dados.valorTotal())
                .modalidade(dados.modalidade())
                .dataRegistro(dados.dataRegistro())
                .arquivado(false)
                .build();

        transacaoRepository.save(transacao);

        int parcelas = (dados.modalidade() == Modalidade.A_VISTA || dados.quantidadeParcelas() == null)
                ? 1 : dados.quantidadeParcelas();

        BigDecimal valorParcela = dados.valorTotal().divide(new BigDecimal(parcelas), 2, java.math.RoundingMode.HALF_UP);

        // Se o frontend não enviar a periodicidade, assumimos MENSAL por padrão
        Periodicidade periodo = dados.periodicidade() != null ? dados.periodicidade() : Periodicidade.MENSAL;

        for (int i = 1; i <= parcelas; i++) {

            // Mágica do Java 21 para calcular a data exata da parcela!
            LocalDate dataCalculada = switch (periodo) {
                case DIARIA -> dados.dataRegistro().plusDays(i - 1);
                case SEMANAL -> dados.dataRegistro().plusWeeks(i - 1);
                case MENSAL -> dados.dataRegistro().plusMonths(i - 1);
                case ANUAL -> dados.dataRegistro().plusYears(i - 1);
            };

            var fluxo = FluxoFinanceiro.builder()
                    .transacao(transacao)
                    .numeroParcela(i)
                    .valorParcela(valorParcela)
                    .dataCompetencia(dataCalculada)
                    .status(com.controlefinanceiro.model.enums.StatusParcela.PENDENTE)
                    .build();

            fluxoRepository.save(fluxo);
        }

        return transacao;
    }

    // Adicione estes métodos dentro do TransacaoService:

    public List<Transacao> buscarPorUsuario(String email) {
        return transacaoRepository.findByUsuarioEmailAndArquivadoFalse(email);
    }

    public Transacao buscarDetalhes(Long id, String email) {
        // Tenta achar a transação. Se o ID não existir ou for de outra pessoa, dá erro 404 (Not Found)
        return transacaoRepository.buscarPorIdEEmailComParcelas(id, email)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada ou acesso negado."));
    }

    // Adicione este método dentro do TransacaoService

    @Transactional
    public FluxoFinanceiro atualizarStatusParcela(Long idParcela, DadosAtualizacaoParcela dados, String email) {
        // 1. Busca a parcela garantindo a segurança
        var parcela = fluxoRepository.buscarPorIdEEmailUsuario(idParcela, email)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada ou acesso negado."));

        // 2. Lógica de atualização
        if (dados.pago() != null && dados.pago()) {
            parcela.setStatus(com.controlefinanceiro.model.enums.StatusParcela.PAGO);
            // Se o frontend não mandar a data, assumimos a data de hoje
            parcela.setDataPagamentoEfetivo(dados.dataPagamentoEfetivo() != null ? dados.dataPagamentoEfetivo() : java.time.LocalDate.now());
        } else {
            // Permite "desfazer" um pagamento, voltando a parcela para PENDENTE
            parcela.setStatus(com.controlefinanceiro.model.enums.StatusParcela.PENDENTE);
            parcela.setDataPagamentoEfetivo(null);
        }

        return parcela; // Como estamos usando @Transactional, o Hibernate salva a alteração automaticamente no fim do método!
    }

    @Transactional
    public void alternarArquivamento(Long id, String emailUsuario) {
        // Busca a transação garantindo que ela pertence ao usuário logado
        var transacao = transacaoRepository.getReferenceById(id);

        // Verifica se a transação pertence ao usuário (Segurança extra)
        if (!transacao.getUsuario().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("Acesso negado");
        }

        // A MÁGICA: Inverte o valor atual. Se for true vira false, se for false vira true.
        transacao.setArquivado(!transacao.getArquivado());
    }

    @Transactional
    public void deletarTransacao(Long id, String email) {
        Transacao transacao = buscarDetalhes(id, email);

        // O Hibernate vai deletar a transação e, graças ao CASCADE que fizemos no SQL,
        // vai deletar todas as parcelas (fluxos_financeiros) ligadas a ela automaticamente!
        transacaoRepository.delete(transacao);
    }

    public List<DadosListagemTransacao> listarArquivadas(String email) {
        return transacaoRepository.findByUsuarioEmailAndArquivadoTrue(email)
                .stream()
                .map(DadosListagemTransacao::new)
                .toList();
    }
}