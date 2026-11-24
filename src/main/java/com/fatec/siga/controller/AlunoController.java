package com.fatec.siga.controller;

import com.fatec.siga.model.Aula;
import com.fatec.siga.repository.AulaRepository;
import com.fatec.siga.service.PresencaService;
import com.fatec.siga.service.RosterDemo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    private static final Long ALUNO_LOGADO = 1L;

    private final AulaRepository aulaRepository;
    private final PresencaService presencaService;
    private final RosterDemo roster;

    public AlunoController(AulaRepository aulaRepository, PresencaService presencaService, RosterDemo roster) {
        this.aulaRepository = aulaRepository;
        this.presencaService = presencaService;
        this.roster = roster;
    }

    @GetMapping("/aulas")
    public String minhasAulas(Model model) {
        List<Long> turmas = roster.turmasDoAluno(ALUNO_LOGADO);
        List<Aula> aulas = turmas.isEmpty() ? List.of() : aulaRepository.findByTurmaIdInOrderByDataDesc(turmas);
        model.addAttribute("aulas", aulas);
        return "aluno/minhas-aulas";
    }

    @GetMapping("/frequencia")
    public String frequencia(Model model) {
        model.addAttribute("frequencias", presencaService.calcularFrequenciaPorTurma(ALUNO_LOGADO));
        model.addAttribute("geral", presencaService.percentualGeral(ALUNO_LOGADO));
        return "aluno/frequencia";
    }
}
