package com.fatec.siga.service;

import com.fatec.siga.dto.FrequenciaDisciplina;
import com.fatec.siga.model.Aula;
import com.fatec.siga.model.Presenca;
import com.fatec.siga.model.StatusAula;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PresencaServiceTest {

    private Aula novaAulaComAlunos() {
        Aula aula = new Aula(101L, "ADS 3 Semestre - PSI", 1L,
                LocalDate.now(), LocalTime.of(19, 0));
        aula.adicionarPresenca(new Presenca(1L, "Henrique Ferreira", "RA001"));
        aula.adicionarPresenca(new Presenca(2L, "Ana Vitoria", "RA002"));
        aula.adicionarPresenca(new Presenca(3L, "Guilherme Souza", "RA003"));
        aula.adicionarPresenca(new Presenca(4L, "Rafael Mendes", "RA004"));
        return aula;
    }

    @Test
    void deveGerarUmaPresencaParaCadaAluno() {
        Aula aula = novaAulaComAlunos();
        assertEquals(4, aula.getTotalAlunos());
        assertEquals(0, aula.getTotalPresentes());
    }

    @Test
    void deveContarPresentesAposMarcar() {
        Aula aula = novaAulaComAlunos();
        aula.getPresencas().get(0).marcar(true);
        aula.getPresencas().get(1).marcar(true);
        aula.getPresencas().get(2).marcar(false);
        aula.getPresencas().get(3).marcar(true);

        assertEquals(3, aula.getTotalPresentes());
        assertEquals(4, aula.getTotalAlunos());
    }

    @Test
    void deveRegistrarHorarioAoMarcarPresenca() {
        Presenca presenca = new Presenca(1L, "Henrique", "RA001");
        assertFalse(presenca.isPresente());
        presenca.marcar(true);
        assertTrue(presenca.isPresente());
        assertTrue(presenca.getRegistradoEm() != null);
    }

    @Test
    void deveAprovarAlunoComFrequenciaIgualOuMaiorQue75() {
        FrequenciaDisciplina alta = new FrequenciaDisciplina(101L, "PSI", 10, 8, 80.0);
        FrequenciaDisciplina exata = new FrequenciaDisciplina(101L, "PSI", 4, 3, 75.0);

        assertTrue(alta.isAprovadoPorFrequencia());
        assertTrue(exata.isAprovadoPorFrequencia());
    }

    @Test
    void deveReprovarAlunoComFrequenciaAbaixoDe75() {
        FrequenciaDisciplina baixa = new FrequenciaDisciplina(101L, "PSI", 10, 5, 50.0);
        assertFalse(baixa.isAprovadoPorFrequencia());
    }

    @Test
    void novaAulaDeveComecarComoAgendada() {
        Aula aula = novaAulaComAlunos();
        assertEquals(StatusAula.AGENDADA, aula.getStatus());
    }
}
