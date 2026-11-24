package com.fatec.siga.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fatec.siga.domain.Turma;
import com.fatec.siga.repository.CursoRepository;
import com.fatec.siga.repository.DisciplinaRepository;
import com.fatec.siga.repository.ProfessorRepository;
import com.fatec.siga.service.TurmaService;
import com.fatec.siga.service.TurmaService.CodigoDuplicadoException;
import com.fatec.siga.service.TurmaService.DadosTurma;
import com.fatec.siga.service.TurmaService.RecursoNaoEncontradoException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/turmas")
public class TurmaController {

	private final TurmaService turmaService;
	private final CursoRepository cursoRepository;
	private final DisciplinaRepository disciplinaRepository;
	private final ProfessorRepository professorRepository;

	public TurmaController(TurmaService turmaService, CursoRepository cursoRepository,
			DisciplinaRepository disciplinaRepository, ProfessorRepository professorRepository) {
		this.turmaService = turmaService;
		this.cursoRepository = cursoRepository;
		this.disciplinaRepository = disciplinaRepository;
		this.professorRepository = professorRepository;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("turmas", turmaService.listarTodas());
		return "turmas/lista";
	}

	@GetMapping("/nova")
	public String nova(Model model) {
		model.addAttribute("turma", new DadosTurma("", null, null, null, null, null, true));
		adicionarOpcoes(model);
		return "turmas/formulario";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		Turma turma = turmaService.buscarPorId(id);
		model.addAttribute("turma", new DadosTurma(
			turma.getCodigo(),
			turma.getAno(),
			turma.getSemestre(),
			turma.getCurso().getId(),
			turma.getDisciplina().getId(),
			turma.getProfessor().getId(),
			turma.isAtiva()
		));
		model.addAttribute("turmaId", id);
		adicionarOpcoes(model);
		return "turmas/formulario";
	}

	@PostMapping
	public String criar(@Valid DadosTurma turma, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("turma", turma);
			adicionarOpcoes(model);
			return "turmas/formulario";
		}

		try {
			turmaService.criar(turma);
			redirectAttributes.addFlashAttribute("mensagem", "Turma criada com sucesso");
			return "redirect:/admin/turmas";
		} catch (CodigoDuplicadoException | RecursoNaoEncontradoException exception) {
			model.addAttribute("turma", turma);
			model.addAttribute("erro", exception.getMessage());
			adicionarOpcoes(model);
			return "turmas/formulario";
		}
	}

	@PostMapping("/{id}")
	public String atualizar(@PathVariable Long id, @Valid DadosTurma turma,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("turma", turma);
			model.addAttribute("turmaId", id);
			adicionarOpcoes(model);
			return "turmas/formulario";
		}

		try {
			turmaService.atualizar(id, turma);
			redirectAttributes.addFlashAttribute("mensagem", "Turma atualizada com sucesso");
			return "redirect:/admin/turmas";
		} catch (CodigoDuplicadoException | RecursoNaoEncontradoException exception) {
			model.addAttribute("turma", turma);
			model.addAttribute("turmaId", id);
			model.addAttribute("erro", exception.getMessage());
			adicionarOpcoes(model);
			return "turmas/formulario";
		}
	}

	@PostMapping("/{id}/excluir")
	public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		turmaService.excluir(id);
		redirectAttributes.addFlashAttribute("mensagem", "Turma excluída com sucesso");
		return "redirect:/admin/turmas";
	}

	private void adicionarOpcoes(Model model) {
		model.addAttribute("cursos", cursoRepository.findAll());
		model.addAttribute("disciplinas", disciplinaRepository.findAll());
		model.addAttribute("professores", professorRepository.findAll());
	}
}
