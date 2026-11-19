package com.fatec.siga.service;

import com.fatec.siga.dto.EnderecoViaCep;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.NoSuchElementException;

@Service
public class ViaCepService {

    private final RestClient restClient;

    public ViaCepService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://viacep.com.br/ws").build();
    }

    public EnderecoViaCep consultar(String cep) {
        String limpo = cep == null ? "" : cep.replaceAll("\\D", "");
        if (limpo.length() != 8) {
            throw new IllegalArgumentException("CEP deve conter 8 digitos.");
        }

        EnderecoViaCep endereco = restClient.get()
                .uri("/{cep}/json/", limpo)
                .retrieve()
                .body(EnderecoViaCep.class);

        if (endereco == null || endereco.naoEncontrado()) {
            throw new NoSuchElementException("CEP nao encontrado: " + limpo);
        }

        return endereco;
    }
}
