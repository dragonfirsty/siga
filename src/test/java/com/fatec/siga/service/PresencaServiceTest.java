package com.fatec.siga.service;

import com.fatec.siga.dto.FrequenciaDisciplina;
import com.fatec.siga.model.Aula;
import com.fatec.siga.model.Presenca;
import com.fatec.siga.repository.AulaRepository;
import com.fatec.siga.repository.PresencaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({PresencaService.class, AulaService.class, RosterDemo.class})
class PresencaServiceTest {

    @Autowired
    private AulaService aulaService;

    @Autowired
    private PresencaService presencaService;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private PresencaRepository presencaRepository;

    private Aula aula;

    @BeforeEach
    void setUp() {
        presencaRepository.deleteAll();
        aulaRepository.deleteAll();
        aula = aulaService.criarAula(101L, LocalDate.now(), LocalTime.of(19, 0),
                LocalTime.of(22, 0), "Introducao a Ajax");
    }

    @Test
    void deveGerarUmaPresencaParaCadaAlunoDaTurma() {
        assertThat(aula.getPresencas()).hasSize(4);
        assertThat(aula.getPresencas()).allMatch(p -> !p.isPresente());
    }

    @Test
    void deveRegistrarPresencasEMarcarAulaComoRealizada() {
        Long id1 = aula.getPresencas().get(0).getAlunoId();
        Long id2 = aula.getPresencas().get(1).getAlunoId();

        presencaService.registrarPresencas(aula.getId(), Map.of(id1, true, id2, true));

        List<Presenca> salvas = presencaService.listarPresencasDaAula(aula.getId());
        long presentes = salvas.stream().filter(Presenca::isPresente).count();

        assertThat(presentes).isEqualTo(2);
    }

    @Test
    void deveCalcularFrequenciaDoAluno() {
        Long alunoId = aula.getPresencas().get(0).getAlunoId();
        presencaService.registrarPresencas(aula.getId(), Map.of(alunoId, true));

        List<FrequenciaDisciplina> frequencias = presencaService.calcularFrequenciaPorTurma(alunoId);

        assertThat(frequencias).hasSize(1);
        assertThat(frequencias.get(0).totalAulas()).isEqualTo(1);
        assertThat(frequencias.get(0).totalPresencas()).isEqualTo(1);
        assertThat(frequencias.get(0).percentual()).isEqualTo(100.0);
        assertThat(frequencias.get(0).isAprovadoPorFrequencia()).isTrue();
    }

    @Test
    void deveReprovarAlunoComFrequenciaBaixa() {
        Long alunoId = aula.getPresencas().get(0).getAlunoId();
        presencaService.registrarPresencas(aula.getId(), Map.of(alunoId, false));

        List<FrequenciaDisciplina> frequencias = presencaService.calcularFrequenciaPorTurma(alunoId);

        assertThat(frequencias.get(0).percentual()).isEqualTo(0.0);
        assertThat(frequencias.get(0).isAprovadoPorFrequencia()).isFalse();
    }
}
