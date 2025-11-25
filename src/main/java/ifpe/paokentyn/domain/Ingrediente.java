/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ifpe.paokentyn.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author isabe
 */
@Entity
@Table(name = "TB_INGREDIENTE")
public class Ingrediente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Column(name = "TXT_NOME", nullable = false, length = 100, unique = true)
    private String nome;

    
    @ManyToMany(mappedBy = "ingredientes", fetch = FetchType.LAZY)
    private List<Pao> paes;

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Pao> getPaes() {
        return paes;
    }

    public void setPaes(List<Pao> paes) {
        this.paes = paes;
    }
}
