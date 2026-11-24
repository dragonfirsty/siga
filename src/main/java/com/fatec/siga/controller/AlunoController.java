package com.fatec.siga.controller;

import com.fatec.siga.model.Aula;
import com.fatec.siga.repository.AulaRepository;
import com.fatec.siga.service.PresencaService;
import com.fatec.siga.service.RosterDemo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

import java.util.List;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    private final AulaRepository aulaRepository;
    private final PresencaService presencaService;
    private final RosterDemo roster;

    public AlunoController(AulaRepository aulaRepository, PresencaService presencaService, RosterDemo roster) {
        this.aulaRepository = aulaRepository;
        this.presencaService = presencaService;
        this.roster = roster;
    }

    @GetMapping("/aulas")
    public String minhasAulas(Model model, Authentication authentication) {
        Long alunoId = buscarAlunoId(authentication);
        if (alunoId == null) {
            return "redirect:/login";
        }
        List<Long> turmas = roster.turmasDoAluno(alunoId);
        List<Aula> aulas = turmas.isEmpty() ? List.of() : aulaRepository.findByTurmaIdInOrderByDataDesc(turmas);
        model.addAttribute("aulas", aulas);
        return "aluno/minhas-aulas";
    }

    @GetMapping("/frequencia")
    public String frequencia(Model model, Authentication authentication) {
        Long alunoId = buscarAlunoId(authentication);
        if (alunoId == null) {
            return "redirect:/login";
        }
        model.addAttribute("frequencias", presencaService.calcularFrequenciaPorTurma(alunoId));
        model.addAttribute("geral", presencaService.percentualGeral(alunoId));
        return "aluno/frequencia";
    }

    private Long buscarAlunoId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return roster.buscarAlunoPorEmail(authentication.getName())
            .map(aluno -> aluno.getId())
            .orElse(null);
    }
}
