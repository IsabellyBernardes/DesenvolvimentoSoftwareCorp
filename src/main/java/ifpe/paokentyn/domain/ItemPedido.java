package ifpe.paokentyn.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "TB_ITEM_PEDIDO")
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    
    @NotNull
    @Min(value = 1)
    @Column(name = "NUM_QUANTIDADE", nullable = false)
    private Integer quantidade;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_PEDIDO", nullable = false, referencedColumnName = "ID")
    @NotNull
    private Pedido pedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PAO", nullable = false, referencedColumnName = "ID")
    @NotNull
    private Pao pao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FORNADA", nullable = true, referencedColumnName = "ID")
    private Fornada fornada;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Pao getPao() { return pao; }
    public void setPao(Pao pao) { this.pao = pao; }
    public Fornada getFornada() { return fornada; }
    public void setFornada(Fornada fornada) { this.fornada = fornada; }
}


