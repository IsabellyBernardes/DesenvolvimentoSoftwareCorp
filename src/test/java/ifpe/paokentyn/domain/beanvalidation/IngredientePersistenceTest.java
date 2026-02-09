package ifpe.paokentyn.domain.beanvalidation;

import ifpe.paokentyn.domain.GenericTest;
import ifpe.paokentyn.domain.Ingrediente;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IngredientePersistenceTest extends GenericTest {

    private Ingrediente criarIngredienteValido() {
        Ingrediente i = new Ingrediente();
        i.setNome("Farinha de Trigo Premium");
        return i;
    }

    @Test
    public void testPersistirIngredienteValido() {
        Ingrediente i = criarIngredienteValido();
        assertDoesNotThrow(() -> {
            em.persist(i);
            em.flush();
        });
    }

    @Test
    public void testPersistirNomeComNumeroDeveFalhar() {
        Ingrediente i = criarIngredienteValido();
        i.setNome("Farinha Tipo 1"); // bean @SemNumero deve pegar o erro

        assertThrows(Exception.class, () -> {
            em.persist(i);
            em.flush();
        }, "Deveria falhar validação @SemNumero");
    }

    @Test
    public void testPersistirNomeDuplicadoDeveFalhar() {
        // 1. Salva o primeiro (Farinha de Trigo Premium)
        Ingrediente i1 = criarIngredienteValido();
        em.persist(i1);
        em.flush();

        // 2. Tenta salvar o segundo com O MESMO NOME
        Ingrediente i2 = new Ingrediente();
        i2.setNome("Farinha de Trigo Premium"); // Duplicado

        assertThrows(PersistenceException.class, () -> {
            em.persist(i2);
            em.flush();
        }, "Deveria falhar ao tentar salvar ingrediente duplicado");
    }
    
    @Test
    public void testAtualizarNomeParaInvalidoDeveFalhar() {
        Ingrediente i = criarIngredienteValido();
        em.persist(i);
        em.flush();

        // 2. Tenta ATUALIZAR para um nome com número (Inválido)
        i.setNome("Trigo 100 porcento"); 

        assertThrows(Exception.class, () -> {
            em.merge(i);
            em.flush();
        }, "Deveria falhar ao atualizar ingrediente para nome com números");
    }
}