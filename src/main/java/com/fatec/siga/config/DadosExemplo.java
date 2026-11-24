package com.fatec.siga.config;

import com.fatec.siga.repository.AulaRepository;
import com.fatec.siga.repository.TurmaRepository;
import com.fatec.siga.service.AulaService;
import com.fatec.siga.service.PresencaService;
import com.fatec.siga.service.RosterDemo;
import com.fatec.siga.dto.AlunoResumo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DadosExemplo implements CommandLineRunner {

    private final AulaService aulaService;
    private final PresencaService presencaService;
    private final AulaRepository aulaRepository;
    private final TurmaRepository turmaRepository;
    private final RosterDemo rosterDemo;

    public DadosExemplo(AulaService aulaService, PresencaService presencaService,
            AulaRepository aulaRepository, TurmaRepository turmaRepository, RosterDemo rosterDemo) {
        this.aulaService = aulaService;
        this.presencaService = presencaService;
        this.aulaRepository = aulaRepository;
        this.turmaRepository = turmaRepository;
        this.rosterDemo = rosterDemo;
    }

    @Override
    public void run(String... args) {
        if (aulaRepository.count() > 0) {
            return;
        }

        List<Long> turmas = turmaRepository.findAll().stream()
            .filter(turma -> turma.isAtiva())
            .map(turma -> turma.getId())
            .limit(2)
            .toList();

        if (turmas.isEmpty()) {
            return;
        }

        Long primeiraTurma = turmas.getFirst();
        criarAulaComPresencas(primeiraTurma, LocalDate.now().minusDays(14),
            "Introducao ao HTML e CSS", List.of(true, true, false));
        criarAulaComPresencas(primeiraTurma, LocalDate.now().minusDays(7),
            "JavaScript e manipulacao do DOM", List.of(true, false, true));

        if (turmas.size() > 1) {
            criarAulaComPresencas(turmas.get(1), LocalDate.now().minusDays(10),
                "Modelagem relacional", List.of(true, true, false));
        }

        aulaService.criarAula(primeiraTurma, LocalDate.now().plusDays(2),
            LocalTime.of(19, 0), LocalTime.of(22, 0), "Consumo de API com Ajax");
    }

    private void criarAulaComPresencas(Long turmaId, LocalDate data,
            String conteudo, List<Boolean> presencas) {
        var aula = aulaService.criarAula(turmaId, data,
            LocalTime.of(19, 0), LocalTime.of(22, 0), conteudo);
        List<AlunoResumo> alunos = rosterDemo.alunosDaTurma(turmaId);
        Map<Long, Boolean> marcacoes = new HashMap<>();

        for (int i = 0; i < alunos.size(); i++) {
            boolean presente = i < presencas.size() && presencas.get(i);
            marcacoes.put(alunos.get(i).id(), presente);
        }

        presencaService.registrarPresencas(aula.getId(), marcacoes);
    }
}
