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

import com.fatec.siga.domain.Turma;
import com.fatec.siga.service.TurmaService;
import com.fatec.siga.service.TurmaService.DadosTurma;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/turmas")
public class TurmaRestController {

	private final TurmaService turmaService;

	public TurmaRestController(TurmaService turmaService) {
		this.turmaService = turmaService;
	}

	@GetMapping
	public List<Turma> listar() {
		return turmaService.listarTodas();
	}

	@GetMapping("/{id}")
	public Turma buscar(@PathVariable Long id) {
		return turmaService.buscarPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Turma criar(@Valid @RequestBody DadosTurma dados) {
		return turmaService.criar(dados);
	}

	@PutMapping("/{id}")
	public Turma atualizar(@PathVariable Long id, @Valid @RequestBody DadosTurma dados) {
		return turmaService.atualizar(id, dados);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long id) {
		turmaService.excluir(id);
	}
}
