package com.fatec.siga.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EnderecoViaCep(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        String ddd,
        @JsonProperty("erro") Boolean erro
) {
    public boolean naoEncontrado() {
        return Boolean.TRUE.equals(erro);
    }
}
