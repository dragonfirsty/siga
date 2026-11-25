package com.fatec.siga.repository;

import com.fatec.siga.model.Aula;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AulaRepository extends JpaRepository<Aula, Long> {

    @EntityGraph(attributePaths = "presencas")
    List<Aula> findByProfessorIdOrderByDataDescHoraInicioDesc(Long professorId);

    @EntityGraph(attributePaths = "presencas")
    Optional<Aula> findById(Long id);

    List<Aula> findByTurmaIdOrderByDataDesc(Long turmaId);

    List<Aula> findByTurmaIdInOrderByDataDesc(List<Long> turmaIds);
}
