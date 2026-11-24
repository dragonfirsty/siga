package com.fatec.siga.service;

import com.fatec.siga.dto.FrequenciaDisciplina;
import com.fatec.siga.model.Aula;
import com.fatec.siga.model.Presenca;
import com.fatec.siga.model.StatusAula;
import com.fatec.siga.repository.AulaRepository;
import com.fatec.siga.repository.PresencaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PresencaService {

    private final AulaRepository aulaRepository;
    private final PresencaRepository presencaRepository;

    public PresencaService(AulaRepository aulaRepository, PresencaRepository presencaRepository) {
        this.aulaRepository = aulaRepository;
        this.presencaRepository = presencaRepository;
    }

    @Transactional
    public void registrarPresencas(Long aulaId, Map<Long, Boolean> presentesPorAluno) {
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new IllegalArgumentException("Aula nao encontrada: " + aulaId));

        for (Presenca presenca : aula.getPresencas()) {
            boolean presente = presentesPorAluno.getOrDefault(presenca.getAlunoId(), false);
            presenca.marcar(presente);
        }

        aula.setStatus(StatusAula.REALIZADA);
        aulaRepository.save(aula);
    }

    public List<Presenca> listarPresencasDaAula(Long aulaId) {
        return presencaRepository.findByAulaId(aulaId);
    }

    public List<FrequenciaDisciplina> calcularFrequenciaPorTurma(Long alunoId) {
        List<Presenca> presencas = presencaRepository.findByAlunoId(alunoId);

        Map<Long, List<Presenca>> porTurma = new LinkedHashMap<>();
        for (Presenca p : presencas) {
            if (p.getAula().getStatus() == StatusAula.REALIZADA) {
                porTurma.computeIfAbsent(p.getAula().getTurmaId(), k -> new ArrayList<>()).add(p);
            }
        }

        List<FrequenciaDisciplina> resultado = new ArrayList<>();
        for (Map.Entry<Long, List<Presenca>> entry : porTurma.entrySet()) {
            List<Presenca> lista = entry.getValue();
            int total = lista.size();
            int presentes = (int) lista.stream().filter(Presenca::isPresente).count();
            double percentual = total == 0 ? 0.0 : (presentes * 100.0) / total;
            String turmaNome = lista.get(0).getAula().getTurmaNome();
            resultado.add(new FrequenciaDisciplina(entry.getKey(), turmaNome, total, presentes,
                    Math.round(percentual * 10.0) / 10.0));
        }

        return resultado;
    }

    public double percentualGeral(Long alunoId) {
        long total = presencaRepository.countByAlunoId(alunoId);
        if (total == 0) return 0.0;
        long presentes = presencaRepository.countByAlunoIdAndPresenteTrue(alunoId);
        return Math.round((presentes * 100.0 / total) * 10.0) / 10.0;
    }
}
