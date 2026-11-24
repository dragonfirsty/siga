package com.fatec.siga.service;

import com.fatec.siga.dto.AlunoResumo;
import com.fatec.siga.dto.TurmaResumo;
import com.fatec.siga.domain.Aluno;
import com.fatec.siga.domain.Matricula;
import com.fatec.siga.domain.Professor;
import com.fatec.siga.domain.Turma;
import com.fatec.siga.repository.MatriculaRepository;
import com.fatec.siga.repository.ProfessorRepository;
import com.fatec.siga.repository.TurmaRepository;
import com.fatec.siga.repository.AlunoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class RosterDemo {

    private final TurmaRepository turmaRepository;
    private final MatriculaRepository matriculaRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;

    public RosterDemo(TurmaRepository turmaRepository, MatriculaRepository matriculaRepository,
            ProfessorRepository professorRepository, AlunoRepository alunoRepository) {
        this.turmaRepository = turmaRepository;
        this.matriculaRepository = matriculaRepository;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional(readOnly = true)
    public List<TurmaResumo> listarTurmasDoProfessor(Long professorId) {
        return turmaRepository.findByProfessorId(professorId).stream()
            .filter(Turma::isAtiva)
            .map(this::converterTurma)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<TurmaResumo> listarTodasTurmas() {
        return turmaRepository.findAll().stream()
            .filter(Turma::isAtiva)
            .map(this::converterTurma)
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<TurmaResumo> buscarTurma(Long turmaId) {
        return turmaRepository.findById(turmaId).map(this::converterTurma);
    }

    @Transactional(readOnly = true)
    public List<AlunoResumo> alunosDaTurma(Long turmaId) {
        return matriculaRepository.findByTurmaId(turmaId).stream()
            .filter(Matricula::isAtiva)
            .map(Matricula::getAluno)
            .map(this::converterAluno)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Long> turmasDoAluno(Long alunoId) {
        return matriculaRepository.findByAlunoId(alunoId).stream()
            .filter(Matricula::isAtiva)
            .map(Matricula::getTurma)
            .map(Turma::getId)
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Professor> buscarProfessorPorEmail(String email) {
        return professorRepository.findByUsuarioEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Aluno> buscarAlunoPorEmail(String email) {
        return alunoRepository.findByUsuarioEmail(email);
    }

    private TurmaResumo converterTurma(Turma turma) {
        return new TurmaResumo(
            turma.getId(),
            turma.getCodigo(),
            turma.getDisciplina().getNome(),
            turma.getProfessor().getId(),
            turma.getProfessor().getUsuario().getNome()
        );
    }

    private AlunoResumo converterAluno(Aluno aluno) {
        return new AlunoResumo(
            aluno.getId(),
            aluno.getUsuario().getNome(),
            aluno.getMatricula()
        );
    }
}
