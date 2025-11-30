package ifpe.paokentyn.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "TB_TAREFA")
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id_tarefa PK
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FUNCIONARIO", nullable = false, referencedColumnName = "ID")
    @NotNull
    private Funcionario funcionario;
    
    @NotBlank
    @Size(max = 500)
    @Column(name = "TXT_DESCRICAO", nullable = false, length = 500)
    private String descricao;
    
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "DT_INICIO", nullable = false)
    private Date dataInicio;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CONCLUSAO")
    private Date dataConclusao;
    
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Future
    @Column(name = "DT_PREVISAO", nullable = false)
    private Date dataPrevisao;
    
    @NotNull
    @Column(name = "FLG_CONCLUIDA", nullable = false)
    private Boolean concluida;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }
    public Date getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(Date dataConclusao) { this.dataConclusao = dataConclusao; }
    public Date getDataPrevisao() { return dataPrevisao; }
    public void setDataPrevisao(Date dataPrevisao) { this.dataPrevisao = dataPrevisao; }
    public Boolean getConcluida() { return concluida; }
    public void setConcluida(Boolean concluida) { this.concluida = concluida; }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Tarefa)) {
            return false;
        }

        Tarefa other = (Tarefa) object;

        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
}
