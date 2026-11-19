package com.fatec.siga.dto;

public record FrequenciaDisciplina(
        Long turmaId,
        String turmaNome,
        int totalAulas,
        int totalPresencas,
        double percentual
) {
    public boolean isAprovadoPorFrequencia() {
        return percentual >= 75.0;
    }
}
