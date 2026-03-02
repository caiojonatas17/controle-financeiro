package com.controlefinanceiro.controller;

import com.controlefinanceiro.dto.DadosDashboardResumo;
import com.controlefinanceiro.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/resumo")
    public ResponseEntity<DadosDashboardResumo> resumoMensal(
            @RequestParam int ano,
            @RequestParam int mes) {

        var emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        var resumo = dashboardService.obterResumoDoMes(ano, mes, emailUsuarioLogado);

        return ResponseEntity.ok(resumo);
    }
}