package ifpe.paokentyn.domain;

import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class IngredienteValidationTest extends AbstractValidationTest {

    private Ingrediente criarIngredienteValido() {
        Ingrediente ingrediente = new Ingrediente();
        
        ingrediente.setNome("Farinha de Trigo Premium");
        
        return ingrediente;
    }

    @Test
    public void testIngredienteValido() {
        Ingrediente ingrediente = criarIngredienteValido();

        Set<ConstraintViolation<Ingrediente>> violacoes = validator.validate(ingrediente);
        assertTrue(violacoes.isEmpty(), "O ingrediente com nome correto deve passar na validação");
    }

    @Test
    public void testNomeEmBrancoInvalido() {
        Ingrediente ingrediente = criarIngredienteValido();
        
        // violando o @NotBlank
        ingrediente.setNome("   "); 

        Set<ConstraintViolation<Ingrediente>> violacoes = validator.validate(ingrediente);
        assertEquals(1, violacoes.size(), "Deveria barrar ingrediente sem nome");
    }

    @Test
    public void testNomeMuitoGrandeInvalido() {
        Ingrediente ingrediente = criarIngredienteValido();
        
        // Criando uma string gigante (140 caracteres) para violar o @Size(max = 100)
        String nomeGigante = "Farinha ".repeat(20); 
        ingrediente.setNome(nomeGigante); 

        Set<ConstraintViolation<Ingrediente>> violacoes = validator.validate(ingrediente);
        assertEquals(1, violacoes.size(), "Deveria barrar nome com mais de 100 caracteres");
    }
}