package com.fatec.siga.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "aula")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "turma_id", nullable = false)
    private Long turmaId;

    @NotBlank
    @Size(max = 160)
    @Column(name = "turma_nome", nullable = false, length = 160)
    private String turmaNome;

    @NotNull
    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    @NotNull
    @Column(nullable = false)
    private LocalDate data;

    @NotNull
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @Size(max = 255)
    @Column(length = 255)
    private String conteudo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private StatusAula status = StatusAula.AGENDADA;

    @OneToMany(mappedBy = "aula", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Presenca> presencas = new ArrayList<>();

    public Aula() {
    }

    public Aula(Long turmaId, String turmaNome, Long professorId, LocalDate data, LocalTime horaInicio) {
        this.turmaId = turmaId;
        this.turmaNome = turmaNome;
        this.professorId = professorId;
        this.data = data;
        this.horaInicio = horaInicio;
    }

    public void adicionarPresenca(Presenca presenca) {
        presenca.setAula(this);
        this.presencas.add(presenca);
    }

    public long getTotalPresentes() {
        return presencas.stream().filter(Presenca::isPresente).count();
    }

    public int getTotalAlunos() {
        return presencas.size();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTurmaId() { return turmaId; }
    public void setTurmaId(Long turmaId) { this.turmaId = turmaId; }

    public String getTurmaNome() { return turmaNome; }
    public void setTurmaNome(String turmaNome) { this.turmaNome = turmaNome; }

    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public StatusAula getStatus() { return status; }
    public void setStatus(StatusAula status) { this.status = status; }

    public List<Presenca> getPresencas() { return presencas; }
    public void setPresencas(List<Presenca> presencas) { this.presencas = presencas; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aula)) return false;
        Aula aula = (Aula) o;
        return id != null && Objects.equals(id, aula.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
