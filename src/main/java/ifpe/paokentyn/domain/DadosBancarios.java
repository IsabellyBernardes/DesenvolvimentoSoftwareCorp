/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ifpe.paokentyn.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * @author isabe
 */
@Entity
@Table(name = "TB_DADOS_BANCARIOS")
public class DadosBancarios implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "TXT_BANCO", nullable = false, length = 100)
    private String banco;

    @NotBlank
    @Column(name = "TXT_AGENCIA", nullable = false, length = 10)
    private String agencia;

    @NotBlank
    @Column(name = "TXT_CONTA", nullable = false, length = 20)
    private String conta;

    @OneToOne(mappedBy = "dadosBancarios", fetch = FetchType.LAZY, optional = false)
    private Funcionario funcionario;

    // --- Getters e Setters ---
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DadosBancarios)) return false;

        DadosBancarios db = (DadosBancarios) o;
        return id.equals(db.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
