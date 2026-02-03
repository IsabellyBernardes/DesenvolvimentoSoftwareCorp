package ifpe.paokentyn.domain;

import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DadosBancariosValidationTest extends AbstractValidationTest {

    private DadosBancarios criarDadosValidos() {
        DadosBancarios dados = new DadosBancarios();
        dados.setBanco("Banco Digital Nubank");
        
        //Agência com 4 dígitos e Conta com hífen
        dados.setAgencia("0001");
        dados.setConta("12345-6"); 
        
        return dados;
    }

    @Test
    public void testDadosBancariosValidos() {
        DadosBancarios dados = criarDadosValidos();

        Set<ConstraintViolation<DadosBancarios>> violacoes = validator.validate(dados);
        assertTrue(violacoes.isEmpty(), "Os dados bancários deveriam ser 100% válidos");
    }

    @Test
    public void testAgenciaComLetrasInvalida() {
        DadosBancarios dados = criarDadosValidos();
        
        dados.setAgencia("00AB"); 

        Set<ConstraintViolation<DadosBancarios>> violacoes = validator.validate(dados);
        
        assertEquals(1, violacoes.size(), "Deveria barrar agência com letras");
    }

    @Test
    public void testNomeDoBancoEmBranco() {
        DadosBancarios dados = criarDadosValidos();
        
        // deixando vazio (@NotBlank)
        dados.setBanco("   "); 

        Set<ConstraintViolation<DadosBancarios>> violacoes = validator.validate(dados);
        assertEquals(1, violacoes.size(), "Deveria barrar nome do banco em branco");
    }
    
    @Test
    public void testContaInvalidaComLetras() {
        DadosBancarios dados = criarDadosValidos();
        
        // colocando letras onde só deveria ter números/hífen
        dados.setConta("123A5-6"); 

        Set<ConstraintViolation<DadosBancarios>> violacoes = validator.validate(dados);
        
        assertEquals(1, violacoes.size(), "Deveria barrar a conta contendo letras");
    }
}