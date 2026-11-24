package com.fatec.siga.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.fatec.siga.domain.Curso;
import com.fatec.siga.repository.CursoRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Service
@Validated
public class CursoService {

	private final CursoRepository cursoRepository;

	public CursoService(CursoRepository cursoRepository) {
		this.cursoRepository = cursoRepository;
	}

	@Transactional(readOnly = true)
	public List<Curso> listarTodos() {
		return cursoRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Curso buscarPorId(Long id) {
		return cursoRepository.findById(id)
			.orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado"));
	}

	@Transactional
	public Curso criar(@Valid DadosCurso dados) {
		validarCodigoDisponivel(dados.codigo(), null);

		Curso curso = new Curso();
		curso.setCodigo(dados.codigo().trim().toUpperCase());
		curso.setNome(dados.nome().trim());
		curso.setAtivo(dados.ativo());
		return cursoRepository.save(curso);
	}

	@Transactional
	public Curso atualizar(Long id, @Valid DadosCurso dados) {
		Curso curso = buscarPorId(id);
		validarCodigoDisponivel(dados.codigo(), id);

		curso.setCodigo(dados.codigo().trim().toUpperCase());
		curso.setNome(dados.nome().trim());
		curso.setAtivo(dados.ativo());
		return cursoRepository.save(curso);
	}

	@Transactional
	public void excluir(Long id) {
		Curso curso = buscarPorId(id);
		cursoRepository.delete(curso);
	}

	private void validarCodigoDisponivel(String codigo, Long id) {
		String codigoNormalizado = codigo.trim().toUpperCase();
		boolean existe;

		if (id == null) {
			existe = cursoRepository.existsByCodigo(codigoNormalizado);
		} else {
			existe = cursoRepository.existsByCodigoAndIdNot(codigoNormalizado, id);
		}

		if (existe) {
			throw new CodigoDuplicadoException("Já existe um curso com este código");
		}
	}

	public record DadosCurso(
		@NotBlank(message = "O código é obrigatório")
		@Size(max = 20, message = "O código deve ter no máximo 20 caracteres")
		String codigo,

		@NotBlank(message = "O nome é obrigatório")
		@Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
		String nome,

		boolean ativo
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
