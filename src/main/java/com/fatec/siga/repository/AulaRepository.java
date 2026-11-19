package com.fatec.siga.repository;

import com.fatec.siga.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

    List<Aula> findByProfessorIdOrderByDataDescHoraInicioDesc(Long professorId);

    List<Aula> findByTurmaIdOrderByDataDesc(Long turmaId);

    List<Aula> findByTurmaIdInOrderByDataDesc(List<Long> turmaIds);
}
