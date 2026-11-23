package com.fatec.siga.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.siga.domain.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

	Optional<Professor> findByRegistro(String registro);

	Optional<Professor> findByUsuarioId(Long usuarioId);

	List<Professor> findByDepartamentoIgnoreCase(String departamento);

	boolean existsByRegistro(String registro);
}
