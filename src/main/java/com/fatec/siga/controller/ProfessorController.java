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
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/professor/aulas")
public class ProfessorController {

    private final AulaService aulaService;
    private final PresencaService presencaService;
    private final RosterDemo roster;

    public ProfessorController(AulaService aulaService, PresencaService presencaService, RosterDemo roster) {
        this.aulaService = aulaService;
        this.presencaService = presencaService;
        this.roster = roster;
    }

    @GetMapping
    public String listarAulas(Model model, Authentication authentication) {
        Long professorId = buscarProfessorId(authentication);
        if (professorId == null) {
            return "redirect:/login";
        }
        model.addAttribute("aulas", aulaService.listarPorProfessor(professorId));
        return "professor/aulas";
    }

    @GetMapping("/nova")
    public String formNovaAula(Model model, Authentication authentication) {
        Long professorId = buscarProfessorId(authentication);
        if (professorId == null) {
            return "redirect:/login";
        }
        model.addAttribute("turmas", roster.listarTurmasDoProfessor(professorId));
        return "professor/nova-aula";
    }

    @PostMapping("/nova")
    public String criarAula(
            @RequestParam Long turmaId,
            @RequestParam String data,
            @RequestParam String horaInicio,
            @RequestParam(required = false) String horaFim,
            @RequestParam(required = false) String conteudo,
            Authentication authentication) {
        if (buscarProfessorId(authentication) == null) {
            return "redirect:/login";
        }

        LocalTime fim = (horaFim == null || horaFim.isBlank()) ? null : LocalTime.parse(horaFim);
        Aula aula = aulaService.criarAula(turmaId, LocalDate.parse(data), LocalTime.parse(horaInicio), fim, conteudo);
        return "redirect:/professor/aulas/" + aula.getId() + "/presenca";
    }

    @GetMapping("/{id}/presenca")
    public String formPresenca(@PathVariable Long id, Model model) {
        Aula aula = aulaService.buscarPorId(id);
        model.addAttribute("aula", aula);
        model.addAttribute("presencas", aula.getPresencas());
        return "professor/registrar-presenca";
    }

    @PostMapping("/{id}/presenca")
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

    private Long buscarProfessorId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return roster.buscarProfessorPorEmail(authentication.getName())
            .map(professor -> professor.getId())
            .orElse(null);
    }
}
