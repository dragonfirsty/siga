package com.fatec.siga.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fatec.siga.domain.Perfil;
import com.fatec.siga.domain.Usuario;
import com.fatec.siga.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

		return new User(
				usuario.getEmail(),
				usuario.getSenha(),
				usuario.isAtivo(),
				true,
				true,
				true,
				obterAuthorities(usuario.getPerfis()));
	}

	private Collection<? extends GrantedAuthority> obterAuthorities(Set<Perfil> perfis) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		for (Perfil perfil : perfis) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + perfil.getNome().name()));
		}
		return authorities;
	}
}
