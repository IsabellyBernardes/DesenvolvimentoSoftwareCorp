package ifpe.paokentyn.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.hibernate.validator.constraints.br.CNPJ;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "TB_PADARIA")
public class Padaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "{padaria.nome.notblank}")
    @Size(max = 255, message = "{padaria.nome.size}") 
    @Column(name = "TXT_NOME", nullable = false, length = 255)
    private String nome;
    
    @Size(min = 9, max = 9, message = "{padaria.cep.size}")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "{padaria.cep.pattern}")
    @Column(name = "TXT_CEP", length = 9)
    private String cep;
    
    @NotBlank(message = "{padaria.cnpj.notblank}")
    @CNPJ(message = "{padaria.cnpj.invalido}")
    @Column(name = "TXT_CNPJ", length = 14, nullable = false, unique = true)
    private String cnpj;
    
    @OneToMany(mappedBy = "padaria", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Funcionario> funcionarios;
    
    @OneToMany(mappedBy = "padaria", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fornada> fornadas;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public List<Funcionario> getFuncionarios() { return funcionarios; }
    public void setFuncionarios(List<Funcionario> funcionarios) { this.funcionarios = funcionarios; }
    public List<Fornada> getFornadas() { return fornadas; }
    public void setFornadas(List<Fornada> fornadas) { this.fornadas = fornadas; }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Padaria)) {
            return false;
        }

        Padaria other = (Padaria) object;

        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
}
