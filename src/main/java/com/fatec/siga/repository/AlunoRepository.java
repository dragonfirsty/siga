package com.fatec.siga.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.siga.domain.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

	Optional<Aluno> findByMatricula(String matricula);

	Optional<Aluno> findByUsuarioId(Long usuarioId);

	List<Aluno> findByCursoId(Long cursoId);

	boolean existsByMatricula(String matricula);
}
