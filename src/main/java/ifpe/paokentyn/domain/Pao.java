package ifpe.paokentyn.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

import ifpe.paokentyn.validation.SemNumero;

@Entity
@Table(name = "TB_PAO")
public class Pao implements Serializable { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") 
    private Long id;

    @NotBlank(message = "{pao.nome.notblank}")
    @Size(max = 100, message = "{pao.nome.size}")
    @SemNumero
    @Column(name = "TXT_NOME_PAO", nullable = false, length = 100)
    private String nomePao;

    @NotNull(message = "{pao.preco.notnull}")
    @Positive(message = "{pao.preco.positive}")
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
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Pao)) {
            return false;
        }

        Pao other = (Pao) object;

        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
    
    // Construtor Vazio
    public Pao() {}

    // Construtor para o SELECT NEW 
    public Pao(String nomePao, Double preco) {
        this.nomePao = nomePao;
        this.preco = preco;
    }
}