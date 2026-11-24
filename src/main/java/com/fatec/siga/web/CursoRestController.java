package com.fatec.siga.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.siga.domain.Curso;
import com.fatec.siga.service.CursoService;
import com.fatec.siga.service.CursoService.DadosCurso;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cursos")
public class CursoRestController {

	private final CursoService cursoService;

	public CursoRestController(CursoService cursoService) {
		this.cursoService = cursoService;
	}

	@GetMapping
	public List<Curso> listar() {
		return cursoService.listarTodos();
	}

	@GetMapping("/{id}")
	public Curso buscar(@PathVariable Long id) {
		return cursoService.buscarPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Curso criar(@Valid @RequestBody DadosCurso dados) {
		return cursoService.criar(dados);
	}

	@PutMapping("/{id}")
	public Curso atualizar(@PathVariable Long id, @Valid @RequestBody DadosCurso dados) {
		return cursoService.atualizar(id, dados);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long id) {
		cursoService.excluir(id);
	}
}
