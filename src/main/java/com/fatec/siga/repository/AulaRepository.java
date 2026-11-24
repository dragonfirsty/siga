package com.fatec.siga.repository;

import com.fatec.siga.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Long> {

    List<Aula> findByProfessorIdOrderByDataDescHoraInicioDesc(Long professorId);

    List<Aula> findByTurmaIdOrderByDataDesc(Long turmaId);

    List<Aula> findByTurmaIdInOrderByDataDesc(List<Long> turmaIds);
}
