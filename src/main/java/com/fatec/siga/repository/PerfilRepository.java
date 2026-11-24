package com.fatec.siga.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.siga.domain.Perfil;
import com.fatec.siga.domain.Perfil.NomePerfil;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

	Optional<Perfil> findByNome(NomePerfil nome);
}
