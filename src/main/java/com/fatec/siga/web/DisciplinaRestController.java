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

import com.fatec.siga.domain.Disciplina;
import com.fatec.siga.service.DisciplinaService;
import com.fatec.siga.service.DisciplinaService.DadosDisciplina;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/disciplinas")
public class DisciplinaRestController {

	private final DisciplinaService disciplinaService;

	public DisciplinaRestController(DisciplinaService disciplinaService) {
		this.disciplinaService = disciplinaService;
	}

	@GetMapping
	public List<Disciplina> listar() {
		return disciplinaService.listarTodas();
	}

	@GetMapping("/{id}")
	public Disciplina buscar(@PathVariable Long id) {
		return disciplinaService.buscarPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Disciplina criar(@Valid @RequestBody DadosDisciplina dados) {
		return disciplinaService.criar(dados);
	}

	@PutMapping("/{id}")
	public Disciplina atualizar(@PathVariable Long id, @Valid @RequestBody DadosDisciplina dados) {
		return disciplinaService.atualizar(id, dados);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long id) {
		disciplinaService.excluir(id);
	}
}
