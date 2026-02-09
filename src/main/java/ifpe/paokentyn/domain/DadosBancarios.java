/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ifpe.paokentyn.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

import ifpe.paokentyn.validation.SemNumero;

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

    @NotBlank(message = "{dadosbancarios.banco.notblank}")
    @Size(max = 100, message = "{dadosbancarios.banco.size}")
    @SemNumero
    @Column(name = "TXT_BANCO", nullable = false, length = 100)
    private String banco;

    @NotBlank(message = "{dadosbancarios.agencia.notblank}")
    @Size(max = 10, message = "{dadosbancarios.agencia.size}")
    @Pattern(regexp = "[0-9\\-]+", message = "{dadosbancarios.agencia.pattern}")
    @Column(name = "TXT_AGENCIA", nullable = false, length = 10)
    private String agencia;

    @NotBlank(message = "{dadosbancarios.conta.notblank}")
    @Size(max = 20, message = "{dadosbancarios.conta.size}")
    @Pattern(regexp = "[0-9\\-]+", message = "{dadosbancarios.conta.pattern}")
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
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DadosBancarios)) {
            return false;
        }

        DadosBancarios other = (DadosBancarios) object;

        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
}
