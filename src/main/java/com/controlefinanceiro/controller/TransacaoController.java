package com.controlefinanceiro.controller;

import com.controlefinanceiro.dto.*;
import com.controlefinanceiro.repository.UsuarioRepository;
import com.controlefinanceiro.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoTransacao> cadastrar(
            @RequestBody DadosCadastroTransacao dados,
            UriComponentsBuilder uriBuilder) {

        // 1. Pega o e-mail do usuário logado diretamente do Token JWT validado
        var emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Busca o objeto Usuário completo no banco de dados
        var usuario = usuarioRepository.findByEmail(emailUsuarioLogado).orElseThrow();

        // 3. Manda para o Service fazer a mágica do parcelamento
        var transacao = service.registrarTransacao(dados, usuario);

        // 4. Boa prática REST: Retorna status 201 (Created) e a URL onde o recurso pode ser acessado
        var uri = uriBuilder.path("/api/transacoes/{id}").buildAndExpand(transacao.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoTransacao(transacao));
    }

    @GetMapping
    public ResponseEntity<List<DadosListagemTransacao>> listar(
            @RequestParam() String tipoTransacao,
            @RequestParam(required = false) Long contaId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String modalidade,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        // Pega o email do usuário logado pelo Token
        var emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        // Busca apenas as transações ativas (não arquivadas) deste usuário
        var transacoes = service.buscarPorUsuario(emailUsuarioLogado, tipoTransacao, contaId, categoriaId, modalidade, dataInicio, dataFim); // Vamos criar isso no Service!

        // Converte as Entidades para o DTO de listagem
        var listagem = transacoes.stream().map(DadosListagemTransacao::new).toList();

        return ResponseEntity.ok(listagem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosTransacaoCompleta> detalhar(@PathVariable Long id) {
        var emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        // Busca a transação específica com todas as parcelas
        var transacao = service.buscarDetalhes(id, emailUsuarioLogado);

        return ResponseEntity.ok(new DadosTransacaoCompleta(transacao));
    }

    // Adicione este método dentro do TransacaoController

    @PutMapping("/parcelas/{idParcela}")
    public ResponseEntity<DadosParcela> atualizarParcela(
            @PathVariable Long idParcela,
            @RequestBody DadosAtualizacaoParcela dados) {

        var emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        var parcelaAtualizada = service.atualizarStatusParcela(idParcela, dados, emailUsuarioLogado);

        // Retornamos os dados da parcela atualizados para o Vue.js atualizar o ecrã em tempo real
        return ResponseEntity.ok(new DadosParcela(parcelaAtualizada));
    }


    @PatchMapping("/{id}/arquivar")
    public ResponseEntity<Void> arquivar(@PathVariable Long id) {
        var emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        service.alternarArquivamento(id, emailUsuarioLogado);

        // Retorna 204 No Content (Sucesso, mas sem corpo de resposta)
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        var emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        service.deletarTransacao(id, emailUsuarioLogado);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/arquivadas")
    public ResponseEntity<List<DadosListagemTransacao>> listarArquivadas() {
        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        var arquivadas = service.listarArquivadas(emailLogado);
        return ResponseEntity.ok(arquivadas);
    }

}