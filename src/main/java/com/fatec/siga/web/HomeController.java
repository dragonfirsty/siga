package com.fatec.siga.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String inicio(Authentication authentication) {
		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
		if (isAdmin) {
			return "redirect:/admin";
		}

		boolean isProfessor = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_PROFESSOR"));
		if (isProfessor) {
			return "redirect:/professor";
		}

		boolean isAluno = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ALUNO"));
		if (isAluno) {
			return "redirect:/aluno";
		}

		return "redirect:/login";
	}

	@GetMapping("/admin")
	public String administrador() {
		return "admin/inicio";
	}

	@GetMapping("/professor")
	public String professor() {
		return "professor/inicio";
	}

	@GetMapping("/aluno")
	public String aluno() {
		return "aluno/inicio";
	}

	@GetMapping("/inicio")
	public String demonstracao() {
		return "index";
	}
}
