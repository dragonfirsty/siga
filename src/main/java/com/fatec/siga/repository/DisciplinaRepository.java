package com.fatec.siga.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.siga.domain.Disciplina;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

	@EntityGraph(attributePaths = "curso")
	List<Disciplina> findAll();

	Optional<Disciplina> findByCodigo(String codigo);

	List<Disciplina> findByCursoId(Long cursoId);

	boolean existsByCodigo(String codigo);

	boolean existsByCodigoAndIdNot(String codigo, Long id);
}
