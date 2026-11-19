package com.fatec.siga.controller;

import com.fatec.siga.model.Aula;
import com.fatec.siga.service.AulaService;
import com.fatec.siga.service.PresencaService;
import com.fatec.siga.service.RosterDemo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    private static final Long PROFESSOR_LOGADO = 1L;

    private final AulaService aulaService;
    private final PresencaService presencaService;
    private final RosterDemo roster;

    public ProfessorController(AulaService aulaService, PresencaService presencaService, RosterDemo roster) {
        this.aulaService = aulaService;
        this.presencaService = presencaService;
        this.roster = roster;
    }

    @GetMapping("/aulas")
    public String listarAulas(Model model) {
        model.addAttribute("aulas", aulaService.listarPorProfessor(PROFESSOR_LOGADO));
        return "professor/aulas";
    }

    @GetMapping("/aulas/nova")
    public String formNovaAula(Model model) {
        model.addAttribute("turmas", roster.listarTurmasDoProfessor(PROFESSOR_LOGADO));
        return "professor/nova-aula";
    }

    @PostMapping("/aulas/nova")
    public String criarAula(
            @RequestParam Long turmaId,
            @RequestParam String data,
            @RequestParam String horaInicio,
            @RequestParam(required = false) String horaFim,
            @RequestParam(required = false) String conteudo) {

        LocalTime fim = (horaFim == null || horaFim.isBlank()) ? null : LocalTime.parse(horaFim);
        Aula aula = aulaService.criarAula(turmaId, LocalDate.parse(data), LocalTime.parse(horaInicio), fim, conteudo);
        return "redirect:/professor/aulas/" + aula.getId() + "/presenca";
    }

    @GetMapping("/aulas/{id}/presenca")
    public String formPresenca(@PathVariable Long id, Model model) {
        Aula aula = aulaService.buscarPorId(id);
        model.addAttribute("aula", aula);
        model.addAttribute("presencas", aula.getPresencas());
        return "professor/registrar-presenca";
    }

    @PostMapping("/aulas/{id}/presenca")
    public String salvarPresenca(@PathVariable Long id, @RequestParam Map<String, String> params) {
        Aula aula = aulaService.buscarPorId(id);
        Map<Long, Boolean> marcacoes = new HashMap<>();
        for (var presenca : aula.getPresencas()) {
            boolean presente = params.containsKey("presente_" + presenca.getAlunoId());
            marcacoes.put(presenca.getAlunoId(), presente);
        }
        presencaService.registrarPresencas(id, marcacoes);
        return "redirect:/professor/aulas";
    }
}
