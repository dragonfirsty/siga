package com.fatec.siga.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.siga.domain.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

	Optional<Curso> findByCodigo(String codigo);

	boolean existsByCodigo(String codigo);

	boolean existsByCodigoAndIdNot(String codigo, Long id);
}
