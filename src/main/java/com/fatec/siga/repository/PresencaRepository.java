package com.fatec.siga.repository;

import com.fatec.siga.model.Presenca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresencaRepository extends JpaRepository<Presenca, Long> {

    List<Presenca> findByAulaId(Long aulaId);

    List<Presenca> findByAlunoId(Long alunoId);

    long countByAlunoIdAndPresenteTrue(Long alunoId);

    long countByAlunoId(Long alunoId);
}
