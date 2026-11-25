package com.fatec.siga.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.siga.domain.Turma;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

	@EntityGraph(attributePaths = { "curso", "disciplina", "professor.usuario" })
	List<Turma> findAll();

	List<Turma> findByCursoId(Long cursoId);

	List<Turma> findByDisciplinaId(Long disciplinaId);

	List<Turma> findByProfessorId(Long professorId);

	List<Turma> findByAnoAndSemestre(Integer ano, Integer semestre);

	boolean existsByCodigo(String codigo);

	boolean existsByCodigoAndIdNot(String codigo, Long id);
}
