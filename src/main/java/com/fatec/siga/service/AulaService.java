package com.fatec.siga.service;

import com.fatec.siga.dto.AlunoResumo;
import com.fatec.siga.dto.TurmaResumo;
import com.fatec.siga.model.Aula;
import com.fatec.siga.model.Presenca;
import com.fatec.siga.model.StatusAula;
import com.fatec.siga.repository.AulaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private final RosterDemo roster;

    public AulaService(AulaRepository aulaRepository, RosterDemo roster) {
        this.aulaRepository = aulaRepository;
        this.roster = roster;
    }

    @Transactional
    public Aula criarAula(Long turmaId, LocalDate data, LocalTime horaInicio, LocalTime horaFim, String conteudo) {
        TurmaResumo turma = roster.buscarTurma(turmaId)
                .orElseThrow(() -> new NoSuchElementException("Turma nao encontrada: " + turmaId));

        Aula aula = new Aula(turma.id(), turma.nome() + " - " + turma.disciplina(),
                turma.professorId(), data, horaInicio);
        aula.setHoraFim(horaFim);
        aula.setConteudo(conteudo);
        aula.setStatus(StatusAula.AGENDADA);

        for (AlunoResumo aluno : roster.alunosDaTurma(turmaId)) {
            Presenca presenca = new Presenca(aluno.id(), aluno.nome(), aluno.ra());
            aula.adicionarPresenca(presenca);
        }

        return aulaRepository.save(aula);
    }

    public List<Aula> listarPorProfessor(Long professorId) {
        return aulaRepository.findByProfessorIdOrderByDataDescHoraInicioDesc(professorId);
    }

    public Aula buscarPorId(Long aulaId) {
        return aulaRepository.findById(aulaId)
                .orElseThrow(() -> new NoSuchElementException("Aula nao encontrada: " + aulaId));
    }

    @Transactional
    public Aula concluirAula(Long aulaId) {
        Aula aula = buscarPorId(aulaId);
        aula.setStatus(StatusAula.REALIZADA);
        return aulaRepository.save(aula);
    }
}
