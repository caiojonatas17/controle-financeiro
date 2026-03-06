package com.controlefinanceiro.controller;

import com.controlefinanceiro.dto.DadosDashboardResumo;
import com.controlefinanceiro.dto.DadosNotificacao;
import com.controlefinanceiro.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/resumo")
    public ResponseEntity<DadosDashboardResumo> resumoMensal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        var emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        var resumo = dashboardService.obterResumoPeriodo(dataInicio, dataFim, emailUsuarioLogado);

        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/notificacoes")
    public ResponseEntity<List<DadosNotificacao>> obterNotificacoes() {
        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(dashboardService.obterNotificacoes(emailLogado));
    }
}