package ifpe.paokentyn.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TB_FORNADA")
public class Fornada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PADARIA", nullable = false, referencedColumnName = "ID")
    @NotNull
    private Padaria padaria;
    
    @Temporal(TemporalType.DATE)
    @NotNull
    @Past
    @Column(name = "DT_FORNADA", nullable = false)
    private Date dataFornada;
    
    @Temporal(TemporalType.TIME)
    @NotNull
    @Column(name = "HR_INICIO", nullable = false)
    private Date horaInicio;
    
    @OneToMany(mappedBy = "fornada", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itensPedidos;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Padaria getPadaria() { return padaria; }
    public void setPadaria(Padaria padaria) { this.padaria = padaria; }
    public Date getDataFornada() { return dataFornada; }
    public void setDataFornada(Date dataFornada) { this.dataFornada = dataFornada; }
    public Date getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Date horaInicio) { this.horaInicio = horaInicio; }
     
    public List<ItemPedido> getItensPedidos() { return itensPedidos; }
    public void setItensPedidos(List<ItemPedido> itensPedidos) { this.itensPedidos = itensPedidos; }
    //;add ou removed
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fornada)) return false;

        Fornada forn = (Fornada) o;
        return id.equals(forn.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
}
