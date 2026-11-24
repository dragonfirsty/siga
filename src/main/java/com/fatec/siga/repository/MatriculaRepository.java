package com.fatec.siga.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.siga.domain.Matricula;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

	List<Matricula> findByAlunoId(Long alunoId);

	List<Matricula> findByTurmaId(Long turmaId);

	Optional<Matricula> findByAlunoIdAndTurmaId(Long alunoId, Long turmaId);

	boolean existsByAlunoIdAndTurmaId(Long alunoId, Long turmaId);

	boolean existsByAlunoIdAndTurmaIdAndIdNot(Long alunoId, Long turmaId, Long id);
}
