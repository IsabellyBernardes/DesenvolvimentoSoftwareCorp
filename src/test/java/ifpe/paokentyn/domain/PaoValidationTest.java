package ifpe.paokentyn.domain;

import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PaoValidationTest extends AbstractValidationTest {

    private Pao criarPaoValido() {
        Pao pao = new Pao();
        pao.setNomePao("Pão de Queijo");
        pao.setPreco(3.50); 
        return pao;
    }

    @Test
    public void testPaoValido() {
        Pao pao = criarPaoValido();

        Set<ConstraintViolation<Pao>> violacoes = validator.validate(pao);
        assertTrue(violacoes.isEmpty(), "O pão com dados corretos deve ser 100% válido");
    }

    @Test
    public void testPaoComPrecoNegativoInvalido() {
        Pao pao = criarPaoValido();
        
        // Colocando um preço impossível (prejuízo para a padaria)
        pao.setPreco(-5.00); 

        Set<ConstraintViolation<Pao>> violacoes = validator.validate(pao);
        
        // A anotação @Positive ou @Min(0.1) deve barrar isso
        assertEquals(1, violacoes.size(), "Deveria barrar o preço negativo");
    }

    @Test
    public void testPaoComPrecoZeroInvalido() {
        Pao pao = criarPaoValido();
        
        // Pão não pode ser de graça
        pao.setPreco(0.00); 

        Set<ConstraintViolation<Pao>> violacoes = validator.validate(pao);
        assertEquals(1, violacoes.size(), "Deveria barrar o preço igual a zero");
    }

    @Test
    public void testPaoSemNomeInvalido() {
        Pao pao = criarPaoValido();
        
        pao.setNomePao(""); 

        Set<ConstraintViolation<Pao>> violacoes = validator.validate(pao);
        assertEquals(1, violacoes.size(), "Deveria barrar o nome do pão em branco");
    }
}