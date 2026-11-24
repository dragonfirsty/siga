package com.fatec.siga.config;

import com.fatec.siga.repository.AulaRepository;
import com.fatec.siga.service.AulaService;
import com.fatec.siga.service.PresencaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Component
public class DadosExemplo implements CommandLineRunner {

    private final AulaService aulaService;
    private final PresencaService presencaService;
    private final AulaRepository aulaRepository;

    public DadosExemplo(AulaService aulaService, PresencaService presencaService, AulaRepository aulaRepository) {
        this.aulaService = aulaService;
        this.presencaService = presencaService;
        this.aulaRepository = aulaRepository;
    }

    @Override
    public void run(String... args) {
        if (aulaRepository.count() > 0) {
            return;
        }

        var aula1 = aulaService.criarAula(101L, LocalDate.now().minusDays(14),
                LocalTime.of(19, 0), LocalTime.of(22, 0), "Introducao ao HTML e CSS");
        presencaService.registrarPresencas(aula1.getId(), Map.of(1L, true, 2L, true, 3L, false, 4L, true));

        var aula2 = aulaService.criarAula(101L, LocalDate.now().minusDays(7),
                LocalTime.of(19, 0), LocalTime.of(22, 0), "JavaScript e manipulacao do DOM");
        presencaService.registrarPresencas(aula2.getId(), Map.of(1L, true, 2L, false, 3L, true, 4L, true));

        var aula3 = aulaService.criarAula(102L, LocalDate.now().minusDays(10),
                LocalTime.of(19, 0), LocalTime.of(22, 0), "Modelagem relacional");
        presencaService.registrarPresencas(aula3.getId(), Map.of(1L, true, 5L, true, 6L, false));

        aulaService.criarAula(101L, LocalDate.now().plusDays(2),
                LocalTime.of(19, 0), LocalTime.of(22, 0), "Consumo de API com Ajax");
    }
}
