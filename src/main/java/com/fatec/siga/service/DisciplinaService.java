package com.fatec.siga.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.siga.domain.Curso;
import com.fatec.siga.domain.Disciplina;
import com.fatec.siga.repository.CursoRepository;
import com.fatec.siga.repository.DisciplinaRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Service
public class DisciplinaService {

	private final DisciplinaRepository disciplinaRepository;
	private final CursoRepository cursoRepository;

	public DisciplinaService(DisciplinaRepository disciplinaRepository, CursoRepository cursoRepository) {
		this.disciplinaRepository = disciplinaRepository;
		this.cursoRepository = cursoRepository;
	}

	@Transactional(readOnly = true)
	public List<Disciplina> listarTodas() {
		return disciplinaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Disciplina buscarPorId(Long id) {
		return disciplinaRepository.findById(id)
			.orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina não encontrada"));
	}

	@Transactional
	public Disciplina criar(@Valid DadosDisciplina dados) {
		validarCodigoDisponivel(dados.codigo(), null);
		Curso curso = buscarCurso(dados.cursoId());

		Disciplina disciplina = new Disciplina();
		disciplina.setCodigo(dados.codigo().trim().toUpperCase());
		disciplina.setNome(dados.nome().trim());
		disciplina.setCargaHoraria(dados.cargaHoraria());
		disciplina.setCurso(curso);
		return disciplinaRepository.save(disciplina);
	}

	@Transactional
	public Disciplina atualizar(Long id, @Valid DadosDisciplina dados) {
		Disciplina disciplina = buscarPorId(id);
		validarCodigoDisponivel(dados.codigo(), id);
		Curso curso = buscarCurso(dados.cursoId());

		disciplina.setCodigo(dados.codigo().trim().toUpperCase());
		disciplina.setNome(dados.nome().trim());
		disciplina.setCargaHoraria(dados.cargaHoraria());
		disciplina.setCurso(curso);
		return disciplinaRepository.save(disciplina);
	}

	@Transactional
	public void excluir(Long id) {
		Disciplina disciplina = buscarPorId(id);
		disciplinaRepository.delete(disciplina);
	}

	private Curso buscarCurso(Long cursoId) {
		return cursoRepository.findById(cursoId)
			.orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado"));
	}

	private void validarCodigoDisponivel(String codigo, Long id) {
		String codigoNormalizado = codigo.trim().toUpperCase();
		boolean existe = id == null
			? disciplinaRepository.existsByCodigo(codigoNormalizado)
			: disciplinaRepository.existsByCodigoAndIdNot(codigoNormalizado, id);

		if (existe) {
			throw new CodigoDuplicadoException("Já existe uma disciplina com este código");
		}
	}

	public record DadosDisciplina(
		@NotBlank(message = "O código é obrigatório")
		@Size(max = 20, message = "O código deve ter no máximo 20 caracteres")
		String codigo,

		@NotBlank(message = "O nome é obrigatório")
		@Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
		String nome,

		@NotNull(message = "A carga horária é obrigatória")
		@Positive(message = "A carga horária deve ser maior que zero")
		Integer cargaHoraria,

		@NotNull(message = "Selecione um curso")
		Long cursoId
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
