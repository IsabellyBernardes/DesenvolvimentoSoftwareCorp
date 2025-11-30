package ifpe.paokentyn.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "TB_PAO")
public class Pao implements Serializable { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") 
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "TXT_NOME_PAO", nullable = false, length = 100)
    private String nomePao;

    @Positive
    @NotNull
    @Column(name = "NUM_PRECO", nullable = false)
    private Double preco;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "BIN_IMAGEM")
    private byte[] imagem;

    @OneToMany(mappedBy = "pao", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itensPedidos;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TB_PAO_INGREDIENTE",
            joinColumns = @JoinColumn(name = "ID_PAO"),
            inverseJoinColumns = @JoinColumn(name = "ID_INGREDIENTE")
    )
    private List<Ingrediente> ingredientes;


    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomePao() { return nomePao; }
    public void setNomePao(String nomePao) { this.nomePao = nomePao; }
    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
    public byte[] getImagem() { return imagem; }
    public void setImagem(byte[] imagem) { this.imagem = imagem; }
    public List<ItemPedido> getItensPedidos() { return itensPedidos; }
    public void setItensPedidos(List<ItemPedido> itensPedidos) { this.itensPedidos = itensPedidos; }
    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }
    public void setIngredientes(List<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pao)) return false;

        Pao p = (Pao) o;
        return id.equals(p.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}