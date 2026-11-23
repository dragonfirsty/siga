package com.fatec.siga.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fatec.siga.domain.Perfil;
import com.fatec.siga.domain.Perfil.NomePerfil;
import com.fatec.siga.domain.Usuario;
import com.fatec.siga.repository.PerfilRepository;
import com.fatec.siga.repository.UsuarioRepository;

@Component
public class DadosIniciaisConfig implements ApplicationRunner {

	private final PerfilRepository perfilRepository;
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public DadosIniciaisConfig(
			PerfilRepository perfilRepository,
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder) {
		this.perfilRepository = perfilRepository;
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(ApplicationArguments args) {
		Perfil admin = obterOuCriarPerfil(NomePerfil.ADMIN);
		Perfil professor = obterOuCriarPerfil(NomePerfil.PROFESSOR);
		Perfil aluno = obterOuCriarPerfil(NomePerfil.ALUNO);

		criarUsuario("Administrador SIGA", "admin@siga.local", "admin123", admin);
		criarUsuario("Professor SIGA", "professor@siga.local", "professor123", professor);
		criarUsuario("Aluno SIGA", "aluno@siga.local", "aluno123", aluno);
	}

	private Perfil obterOuCriarPerfil(NomePerfil nome) {
		return perfilRepository.findByNome(nome)
				.orElseGet(() -> {
					Perfil perfil = new Perfil();
					perfil.setNome(nome);
					return perfilRepository.save(perfil);
				});
	}

	private void criarUsuario(String nome, String email, String senha, Perfil perfil) {
		if (usuarioRepository.existsByEmail(email)) {
			return;
		}

		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		usuario.setEmail(email);
		usuario.setSenha(passwordEncoder.encode(senha));
		usuario.setAtivo(true);
		usuario.setPerfis(new HashSet<>(Set.of(perfil)));
		usuarioRepository.save(usuario);
	}
}
