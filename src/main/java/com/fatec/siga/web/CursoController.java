package com.fatec.siga.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fatec.siga.domain.Curso;
import com.fatec.siga.service.CursoService;
import com.fatec.siga.service.CursoService.CodigoDuplicadoException;
import com.fatec.siga.service.CursoService.DadosCurso;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/cursos")
public class CursoController {

	private final CursoService cursoService;

	public CursoController(CursoService cursoService) {
		this.cursoService = cursoService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("cursos", cursoService.listarTodos());
		return "cursos/lista";
	}

	@GetMapping("/novo")
	public String novo(Model model) {
		model.addAttribute("curso", new DadosCurso("", "", true));
		return "cursos/formulario";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		Curso curso = cursoService.buscarPorId(id);
		model.addAttribute("curso", new DadosCurso(
			curso.getCodigo(),
			curso.getNome(),
			curso.isAtivo()
		));
		model.addAttribute("cursoId", id);
		return "cursos/formulario";
	}

	@PostMapping
	public String criar(@Valid DadosCurso curso, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("curso", curso);
			return "cursos/formulario";
		}

		try {
			cursoService.criar(curso);
			redirectAttributes.addFlashAttribute("mensagem", "Curso criado com sucesso");
			return "redirect:/admin/cursos";
		} catch (CodigoDuplicadoException exception) {
			model.addAttribute("curso", curso);
			model.addAttribute("erro", exception.getMessage());
			return "cursos/formulario";
		}
	}

	@PostMapping("/{id}")
	public String atualizar(@PathVariable Long id, @Valid DadosCurso curso,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("curso", curso);
			model.addAttribute("cursoId", id);
			return "cursos/formulario";
		}

		try {
			cursoService.atualizar(id, curso);
			redirectAttributes.addFlashAttribute("mensagem", "Curso atualizado com sucesso");
			return "redirect:/admin/cursos";
		} catch (CodigoDuplicadoException exception) {
			model.addAttribute("curso", curso);
			model.addAttribute("cursoId", id);
			model.addAttribute("erro", exception.getMessage());
			return "cursos/formulario";
		}
	}

	@PostMapping("/{id}/excluir")
	public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		cursoService.excluir(id);
		redirectAttributes.addFlashAttribute("mensagem", "Curso excluído com sucesso");
		return "redirect:/admin/cursos";
	}
}
