package ifpe.paokentyn.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "TB_PAO")
public class Pao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id_pao PK
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "TXT_NOME_PAO", nullable = false, length = 100)
    private String nomePao;
    
    @Positive
    @NotNull
    @Column(name = "NUM_PRECO", nullable = false)
    private Double preco;
    
    @OneToMany(mappedBy = "pao", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval   = true)
    private List<ItemPedido> itensPedidos;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomePao() { return nomePao; }
    public void setNomePao(String nomePao) { this.nomePao = nomePao; }
    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
    
    // Métodos de Coleção 
    public List<ItemPedido> getItensPedidos() { return itensPedidos; }
    public void setItensPedidos(List<ItemPedido> itensPedidos) { this.itensPedidos = itensPedidos; }
}
