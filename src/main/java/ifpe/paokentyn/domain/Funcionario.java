package ifpe.paokentyn.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.br.CPF;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TB_FUNCIONARIO")
public class Funcionario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{funcionario.padaria.notnull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PADARIA", nullable = false, referencedColumnName = "ID")
    private Padaria padaria;

    @NotBlank(message = "{funcionario.nome.notblank}")
    @Size(max = 255, message = "{funcionario.nome.size}")
    @Column(name = "TXT_NOME", nullable = false, length = 255)
    private String nome;

    @NotBlank(message = "{funcionario.cpf.notblank}")
    @CPF(message = "{funcionario.cpf.invalido}")
    @Column(name = "TXT_CPF", nullable = false, length = 14)
    private String cpf;
    
    @NotBlank(message = "{funcionario.email.notblank}")
    @Email(message = "{funcionario.email.invalido}")
    @Column(name = "TXT_EMAIL", nullable = false, length = 150)
    private String email;
    
    @NotBlank(message = "{funcionario.cargo.notblank}")
    @Size(max = 50, message = "{funcionario.cargo.size}")
    @Column(name = "TXT_CARGO", nullable = false, length = 50)
    private String cargo;

    @Past(message = "{funcionario.datacontratacao.past}")
    @Temporal(TemporalType.DATE)
    @Column(name = "DT_CONTRATACAO")
    private Date dataContratacao;

    @DecimalMin(value = "1000.00", message = "{funcionario.salario.min}")
    @Column(name = "NUM_SALARIO")
    private Double salario;

    @OneToMany(mappedBy = "funcionario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarefa> tarefas;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ID_DADOS_BANCARIOS", referencedColumnName = "ID", unique = true)
    private DadosBancarios dadosBancarios;

    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Padaria getPadaria() {
        return padaria;
    }

    public void setPadaria(Padaria padaria) {
        this.padaria = padaria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Date getDataContratacao() {
        return dataContratacao;
    }

    public void setDataContratacao(Date dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public DadosBancarios getDadosBancarios() {
        return dadosBancarios;
    }

    public void setDadosBancarios(DadosBancarios dadosBancarios) {
        this.dadosBancarios = dadosBancarios;
        if (dadosBancarios != null) {
            dadosBancarios.setFuncionario(this);
        }
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Funcionario)) {
            return false;
        }

        Funcionario other = (Funcionario) object;

        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

}
