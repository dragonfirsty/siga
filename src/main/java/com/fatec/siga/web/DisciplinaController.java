package com.fatec.siga.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fatec.siga.domain.Disciplina;
import com.fatec.siga.repository.CursoRepository;
import com.fatec.siga.service.DisciplinaService;
import com.fatec.siga.service.DisciplinaService.CodigoDuplicadoException;
import com.fatec.siga.service.DisciplinaService.DadosDisciplina;
import com.fatec.siga.service.DisciplinaService.RecursoNaoEncontradoException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/disciplinas")
public class DisciplinaController {

	private final DisciplinaService disciplinaService;
	private final CursoRepository cursoRepository;

	public DisciplinaController(DisciplinaService disciplinaService, CursoRepository cursoRepository) {
		this.disciplinaService = disciplinaService;
		this.cursoRepository = cursoRepository;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("disciplinas", disciplinaService.listarTodas());
		return "disciplinas/lista";
	}

	@GetMapping("/nova")
	public String nova(Model model) {
		model.addAttribute("disciplina", new DadosDisciplina("", "", null, null));
		model.addAttribute("cursos", cursoRepository.findAll());
		return "disciplinas/formulario";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		Disciplina disciplina = disciplinaService.buscarPorId(id);
		model.addAttribute("disciplina", new DadosDisciplina(
			disciplina.getCodigo(),
			disciplina.getNome(),
			disciplina.getCargaHoraria(),
			disciplina.getCurso().getId()
		));
		model.addAttribute("disciplinaId", id);
		model.addAttribute("cursos", cursoRepository.findAll());
		return "disciplinas/formulario";
	}

	@PostMapping
	public String criar(@Valid DadosDisciplina disciplina, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("disciplina", disciplina);
			model.addAttribute("cursos", cursoRepository.findAll());
			return "disciplinas/formulario";
		}

		try {
			disciplinaService.criar(disciplina);
			redirectAttributes.addFlashAttribute("mensagem", "Disciplina criada com sucesso");
			return "redirect:/admin/disciplinas";
		} catch (CodigoDuplicadoException | RecursoNaoEncontradoException exception) {
			model.addAttribute("disciplina", disciplina);
			model.addAttribute("cursos", cursoRepository.findAll());
			model.addAttribute("erro", exception.getMessage());
			return "disciplinas/formulario";
		}
	}

	@PostMapping("/{id}")
	public String atualizar(@PathVariable Long id, @Valid DadosDisciplina disciplina,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("disciplina", disciplina);
			model.addAttribute("disciplinaId", id);
			model.addAttribute("cursos", cursoRepository.findAll());
			return "disciplinas/formulario";
		}

		try {
			disciplinaService.atualizar(id, disciplina);
			redirectAttributes.addFlashAttribute("mensagem", "Disciplina atualizada com sucesso");
			return "redirect:/admin/disciplinas";
		} catch (CodigoDuplicadoException | RecursoNaoEncontradoException exception) {
			model.addAttribute("disciplina", disciplina);
			model.addAttribute("disciplinaId", id);
			model.addAttribute("cursos", cursoRepository.findAll());
			model.addAttribute("erro", exception.getMessage());
			return "disciplinas/formulario";
		}
	}

	@PostMapping("/{id}/excluir")
	public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		disciplinaService.excluir(id);
		redirectAttributes.addFlashAttribute("mensagem", "Disciplina excluída com sucesso");
		return "redirect:/admin/disciplinas";
	}
}
