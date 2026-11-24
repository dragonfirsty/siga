package com.fatec.siga.service;

import com.fatec.siga.dto.AlunoResumo;
import com.fatec.siga.dto.TurmaResumo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class RosterDemo {

    private static final Long PROFESSOR_ID = 1L;
    private static final String PROFESSOR_NOME = "Prof. Marcos Santos";

    private static final List<TurmaResumo> TURMAS = List.of(
            new TurmaResumo(101L, "ADS 3 Semestre - Noturno", "Programacao de Sitios Internet", PROFESSOR_ID, PROFESSOR_NOME),
            new TurmaResumo(102L, "ADS 3 Semestre - Noturno", "Banco de Dados", PROFESSOR_ID, PROFESSOR_NOME),
            new TurmaResumo(103L, "ADS 4 Semestre - Noturno", "Engenharia de Software", PROFESSOR_ID, PROFESSOR_NOME)
    );

    private static final Map<Long, List<AlunoResumo>> ALUNOS_POR_TURMA = Map.of(
            101L, List.of(
                    new AlunoResumo(1L, "Henrique Ferreira", "RA001"),
                    new AlunoResumo(2L, "Ana Vitoria Baraldi", "RA002"),
                    new AlunoResumo(3L, "Thayna Camboim", "RA003"),
                    new AlunoResumo(4L, "Guilherme Souza", "RA004")
            ),
            102L, List.of(
                    new AlunoResumo(1L, "Henrique Ferreira", "RA001"),
                    new AlunoResumo(5L, "Rafael Mendes", "RA005"),
                    new AlunoResumo(6L, "Juliana Prado", "RA006")
            ),
            103L, List.of(
                    new AlunoResumo(2L, "Ana Vitoria Baraldi", "RA002"),
                    new AlunoResumo(4L, "Guilherme Souza", "RA004"),
                    new AlunoResumo(7L, "Lucas Oliveira", "RA007")
            )
    );

    public List<TurmaResumo> listarTurmasDoProfessor(Long professorId) {
        return TURMAS.stream().filter(t -> t.professorId().equals(professorId)).toList();
    }

    public List<TurmaResumo> listarTodasTurmas() {
        return TURMAS;
    }

    public Optional<TurmaResumo> buscarTurma(Long turmaId) {
        return TURMAS.stream().filter(t -> t.id().equals(turmaId)).findFirst();
    }

    public List<AlunoResumo> alunosDaTurma(Long turmaId) {
        return ALUNOS_POR_TURMA.getOrDefault(turmaId, List.of());
    }

    public List<Long> turmasDoAluno(Long alunoId) {
        return ALUNOS_POR_TURMA.entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(a -> a.id().equals(alunoId)))
                .map(Map.Entry::getKey)
                .toList();
    }
}
