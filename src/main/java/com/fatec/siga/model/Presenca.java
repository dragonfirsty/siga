package com.fatec.siga.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "presenca", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"aula_id", "aluno_id"})
})
public class Presenca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;

    @NotNull
    @Column(name = "aluno_id", nullable = false)
    private Long alunoId;

    @NotBlank
    @Size(max = 120)
    @Column(name = "aluno_nome", nullable = false, length = 120)
    private String alunoNome;

    @Size(max = 20)
    @Column(length = 20)
    private String ra;

    @Column(nullable = false)
    private boolean presente = false;

    @Column(name = "registrado_em")
    private LocalDateTime registradoEm;

    public Presenca() {
    }

    public Presenca(Long alunoId, String alunoNome, String ra) {
        this.alunoId = alunoId;
        this.alunoNome = alunoNome;
        this.ra = ra;
    }

    public void marcar(boolean presente) {
        this.presente = presente;
        this.registradoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aula getAula() { return aula; }
    public void setAula(Aula aula) { this.aula = aula; }

    public Long getAlunoId() { return alunoId; }
    public void setAlunoId(Long alunoId) { this.alunoId = alunoId; }

    public String getAlunoNome() { return alunoNome; }
    public void setAlunoNome(String alunoNome) { this.alunoNome = alunoNome; }

    public String getRa() { return ra; }
    public void setRa(String ra) { this.ra = ra; }

    public boolean isPresente() { return presente; }
    public void setPresente(boolean presente) { this.presente = presente; }

    public LocalDateTime getRegistradoEm() { return registradoEm; }
    public void setRegistradoEm(LocalDateTime registradoEm) { this.registradoEm = registradoEm; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Presenca)) return false;
        Presenca presenca = (Presenca) o;
        return id != null && Objects.equals(id, presenca.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
