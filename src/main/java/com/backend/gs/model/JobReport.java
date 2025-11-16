package com.backend.gs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "job_report")
public class JobReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idJobReport;

    @NotBlank(message = "O nome da empresa não pode estar vazio.")
    @Size(max = 100, message = "O nome da empresa deve ter no máximo 100 caracteres.")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ0-9 .,&-]+$",
            message = "O nome da empresa contém caracteres inválidos."
    )
    @Column(unique = true, nullable = false)
    private String company;

    @NotBlank(message = "O título não pode estar vazio.")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres.")
    @Column(unique = true, nullable = false)
    private String title;

    @NotBlank(message = "A descrição não pode estar vazia.")
    @Size(min = 50, message = "A descrição deve ter no mínimo 50 caracteres.")
    @Pattern(
            regexp = "^(?!.*(.)\\1{10}).*$",
            message = "A descrição não pode conter caracteres repetidos de forma excessiva."
    )
    @Column(unique = true, nullable = false)
    private String description;

}