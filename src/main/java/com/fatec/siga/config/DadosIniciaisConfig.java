package com.fatec.siga.config;

import java.util.HashSet;
import java.time.LocalDate;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fatec.siga.domain.Perfil;
import com.fatec.siga.domain.Perfil.NomePerfil;
import com.fatec.siga.domain.Usuario;
import com.fatec.siga.domain.Aluno;
import com.fatec.siga.domain.Curso;
import com.fatec.siga.domain.Disciplina;
import com.fatec.siga.domain.Matricula;
import com.fatec.siga.domain.Professor;
import com.fatec.siga.domain.Turma;
import com.fatec.siga.repository.AlunoRepository;
import com.fatec.siga.repository.CursoRepository;
import com.fatec.siga.repository.DisciplinaRepository;
import com.fatec.siga.repository.MatriculaRepository;
import com.fatec.siga.repository.PerfilRepository;
import com.fatec.siga.repository.ProfessorRepository;
import com.fatec.siga.repository.TurmaRepository;
import com.fatec.siga.repository.UsuarioRepository;

@Component
public class DadosIniciaisConfig implements ApplicationRunner {

	private final PerfilRepository perfilRepository;
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final CursoRepository cursoRepository;
	private final DisciplinaRepository disciplinaRepository;
	private final ProfessorRepository professorRepository;
	private final AlunoRepository alunoRepository;
	private final TurmaRepository turmaRepository;
	private final MatriculaRepository matriculaRepository;

	public DadosIniciaisConfig(
			PerfilRepository perfilRepository,
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			CursoRepository cursoRepository,
			DisciplinaRepository disciplinaRepository,
			ProfessorRepository professorRepository,
			AlunoRepository alunoRepository,
			TurmaRepository turmaRepository,
			MatriculaRepository matriculaRepository) {
		this.perfilRepository = perfilRepository;
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.cursoRepository = cursoRepository;
		this.disciplinaRepository = disciplinaRepository;
		this.professorRepository = professorRepository;
		this.alunoRepository = alunoRepository;
		this.turmaRepository = turmaRepository;
		this.matriculaRepository = matriculaRepository;
	}

	@Override
	public void run(ApplicationArguments args) {
		Perfil admin = obterOuCriarPerfil(NomePerfil.ADMIN);
		Perfil professor = obterOuCriarPerfil(NomePerfil.PROFESSOR);
		Perfil aluno = obterOuCriarPerfil(NomePerfil.ALUNO);

		criarUsuario("Administrador SIGA", "admin@siga.local", "admin123", admin);
		Usuario usuarioProfessor = criarUsuario("Professor SIGA", "professor@siga.local", "professor123", professor);
		Usuario usuarioAluno = criarUsuario("Aluno SIGA", "aluno@siga.local", "aluno123", aluno);

		Curso curso = obterOuCriarCurso();
		Disciplina disciplina = obterOuCriarDisciplina(curso);
		Professor professorAcademico = obterOuCriarProfessor(usuarioProfessor);
		Aluno alunoAcademico = obterOuCriarAluno(usuarioAluno, curso);
		Turma turma = obterOuCriarTurma(curso, disciplina, professorAcademico);
		obterOuCriarMatricula(alunoAcademico, turma);
	}

	private Perfil obterOuCriarPerfil(NomePerfil nome) {
		return perfilRepository.findByNome(nome)
				.orElseGet(() -> {
					Perfil perfil = new Perfil();
					perfil.setNome(nome);
					return perfilRepository.save(perfil);
				});
	}

	private Usuario criarUsuario(String nome, String email, String senha, Perfil perfil) {
		Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
		if (usuario != null) {
			return usuario;
		}

		usuario = new Usuario();
		usuario.setNome(nome);
		usuario.setEmail(email);
		usuario.setSenha(passwordEncoder.encode(senha));
		usuario.setAtivo(true);
		usuario.setPerfis(new HashSet<>(Set.of(perfil)));
		return usuarioRepository.save(usuario);
	}

	private Curso obterOuCriarCurso() {
		return cursoRepository.findByCodigo("ADS")
			.orElseGet(() -> {
				Curso curso = new Curso();
				curso.setCodigo("ADS");
				curso.setNome("Análise e Desenvolvimento de Sistemas");
				curso.setAtivo(true);
				return cursoRepository.save(curso);
			});
	}

	private Disciplina obterOuCriarDisciplina(Curso curso) {
		return disciplinaRepository.findByCodigo("DSII")
			.orElseGet(() -> {
				Disciplina disciplina = new Disciplina();
				disciplina.setCodigo("DSII");
				disciplina.setNome("Desenvolvimento de Software II");
				disciplina.setCargaHoraria(80);
				disciplina.setCurso(curso);
				return disciplinaRepository.save(disciplina);
			});
	}

	private Professor obterOuCriarProfessor(Usuario usuario) {
		return professorRepository.findByUsuarioId(usuario.getId())
			.orElseGet(() -> {
				Professor professor = new Professor();
				professor.setRegistro("PROF001");
				professor.setDepartamento("Tecnologia");
				professor.setUsuario(usuario);
				professor.setAtivo(true);
				return professorRepository.save(professor);
			});
	}

	private Aluno obterOuCriarAluno(Usuario usuario, Curso curso) {
		return alunoRepository.findByUsuarioId(usuario.getId())
			.orElseGet(() -> {
				Aluno aluno = new Aluno();
				aluno.setMatricula("ALU001");
				aluno.setUsuario(usuario);
				aluno.setCurso(curso);
				aluno.setAtivo(true);
				return alunoRepository.save(aluno);
			});
	}

	private Turma obterOuCriarTurma(Curso curso, Disciplina disciplina, Professor professor) {
		return turmaRepository.findAll().stream()
			.filter(turma -> "DSII-2026-1".equals(turma.getCodigo()))
			.findFirst()
			.orElseGet(() -> {
				Turma turma = new Turma();
				turma.setCodigo("DSII-2026-1");
				turma.setAno(2026);
				turma.setSemestre(1);
				turma.setCurso(curso);
				turma.setDisciplina(disciplina);
				turma.setProfessor(professor);
				turma.setAtiva(true);
				return turmaRepository.save(turma);
			});
	}

	private void obterOuCriarMatricula(Aluno aluno, Turma turma) {
		if (matriculaRepository.existsByAlunoIdAndTurmaId(aluno.getId(), turma.getId())) {
			return;
		}

		Matricula matricula = new Matricula();
		matricula.setAluno(aluno);
		matricula.setTurma(turma);
		matricula.setDataMatricula(LocalDate.now());
		matricula.setAtiva(true);
		matriculaRepository.save(matricula);
	}
}
