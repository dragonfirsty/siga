package com.fatec.siga.dto;

public record TurmaResumo(
        Long id,
        String nome,
        String disciplina,
        Long professorId,
        String professorNome
) {
}
