package ifpe.paokentyn.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TB_PEDIDO")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    
    @NotNull(message = "{pedido.valortotal.notnull}")
    @PositiveOrZero(message = "{pedido.valortotal.positiveorzero}")
    @Column(name = "NUM_VALOR_TOTAL", nullable = false)
    private Double valorTotal;
    
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "{pedido.data.notnull}")
    @PastOrPresent(message = "{pedido.data.pastorpresent}")
    @Column(name = "DT_PEDIDO", nullable = false)
    private Date dataPedido;
    
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }
    public Date getDataPedido() { return dataPedido; }
    public void setDataPedido(Date dataPedido) { this.dataPedido = dataPedido; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Pedido)) {
            return false;
        }

        Pedido other = (Pedido) object;

        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
 
    public Pedido() {}

    public Pedido(Double valorTotal, java.util.Date dataPedido) {
        this.valorTotal = valorTotal;
        this.dataPedido = dataPedido;
    }
}


