package com.fatec.siga.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.fatec.siga.domain.Curso;
import com.fatec.siga.domain.Disciplina;
import com.fatec.siga.domain.Professor;
import com.fatec.siga.domain.Turma;
import com.fatec.siga.repository.CursoRepository;
import com.fatec.siga.repository.DisciplinaRepository;
import com.fatec.siga.repository.ProfessorRepository;
import com.fatec.siga.repository.TurmaRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Service
@Validated
public class TurmaService {

	private final TurmaRepository turmaRepository;
	private final CursoRepository cursoRepository;
	private final DisciplinaRepository disciplinaRepository;
	private final ProfessorRepository professorRepository;

	public TurmaService(TurmaRepository turmaRepository, CursoRepository cursoRepository,
			DisciplinaRepository disciplinaRepository, ProfessorRepository professorRepository) {
		this.turmaRepository = turmaRepository;
		this.cursoRepository = cursoRepository;
		this.disciplinaRepository = disciplinaRepository;
		this.professorRepository = professorRepository;
	}

	@Transactional(readOnly = true)
	public List<Turma> listarTodas() {
		return turmaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Turma buscarPorId(Long id) {
		return turmaRepository.findById(id)
			.orElseThrow(() -> new RecursoNaoEncontradoException("Turma não encontrada"));
	}

	@Transactional
	public Turma criar(@Valid DadosTurma dados) {
		validarCodigoDisponivel(dados.codigo(), null);

		Turma turma = new Turma();
		aplicarDados(turma, dados);
		return turmaRepository.save(turma);
	}

	@Transactional
	public Turma atualizar(Long id, @Valid DadosTurma dados) {
		Turma turma = buscarPorId(id);
		validarCodigoDisponivel(dados.codigo(), id);

		aplicarDados(turma, dados);
		return turmaRepository.save(turma);
	}

	@Transactional
	public void excluir(Long id) {
		Turma turma = buscarPorId(id);
		turmaRepository.delete(turma);
	}

	private void aplicarDados(Turma turma, DadosTurma dados) {
		Curso curso = cursoRepository.findById(dados.cursoId())
			.orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado"));
		Disciplina disciplina = disciplinaRepository.findById(dados.disciplinaId())
			.orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina não encontrada"));
		Professor professor = professorRepository.findById(dados.professorId())
			.orElseThrow(() -> new RecursoNaoEncontradoException("Professor não encontrado"));

		turma.setCodigo(dados.codigo().trim().toUpperCase());
		turma.setAno(dados.ano());
		turma.setSemestre(dados.semestre());
		turma.setCurso(curso);
		turma.setDisciplina(disciplina);
		turma.setProfessor(professor);
		turma.setAtiva(dados.ativa());
	}

	private void validarCodigoDisponivel(String codigo, Long id) {
		String codigoNormalizado = codigo.trim().toUpperCase();
		boolean existe = id == null
			? turmaRepository.existsByCodigo(codigoNormalizado)
			: turmaRepository.existsByCodigoAndIdNot(codigoNormalizado, id);

		if (existe) {
			throw new CodigoDuplicadoException("Já existe uma turma com este código");
		}
	}

	public record DadosTurma(
		@NotBlank(message = "O código é obrigatório")
		@Size(max = 30, message = "O código deve ter no máximo 30 caracteres")
		String codigo,

		@NotNull(message = "O ano é obrigatório")
		@Min(value = 2020, message = "O ano deve ser igual ou posterior a 2020")
		@Max(value = 2100, message = "O ano deve ser no máximo 2100")
		Integer ano,

		@NotNull(message = "O semestre é obrigatório")
		@Min(value = 1, message = "O semestre deve ser 1 ou 2")
		@Max(value = 2, message = "O semestre deve ser 1 ou 2")
		Integer semestre,

		@NotNull(message = "Selecione um curso")
		Long cursoId,

		@NotNull(message = "Selecione uma disciplina")
		Long disciplinaId,

		@NotNull(message = "Selecione um professor")
		Long professorId,

		boolean ativa
	) {
	}

	public static class RecursoNaoEncontradoException extends RuntimeException {
		public RecursoNaoEncontradoException(String mensagem) {
			super(mensagem);
		}
	}

	public static class CodigoDuplicadoException extends RuntimeException {
		public CodigoDuplicadoException(String mensagem) {
			super(mensagem);
		}
	}
}
