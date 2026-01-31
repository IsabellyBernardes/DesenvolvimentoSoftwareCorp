package ifpe.paokentyn.domain;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IngredienteJPQLTest extends GenericTest {

    /**
     * Teste 1 – Manipulação de String com LIKE
     * Busca ingredientes que contenham a letra "o" (Ovos, Farinha de Trigo, Polvilho - Gergiglim não deve entrar)
     */
    @Test
    public void testBuscarIngredientePorLike() {
        String jpql = """
            SELECT i 
            FROM Ingrediente i 
            WHERE LOWER(i.nome) LIKE '%o%'
        """;

        List<Ingrediente> ingredientes = em.createQuery(jpql, Ingrediente.class)
                                            .getResultList();

        assertEquals(3, ingredientes.size());
    }

    /**
     * Teste 2 – Manipulação de String com LENGTH
     * Busca ingredientes com nome maior que 5 caracteres
     */
    @Test
    public void testBuscarIngredientePorTamanhoDoNome() {
        String jpql = """
            SELECT i 
            FROM Ingrediente i 
            WHERE LENGTH(i.nome) > 5
        """;

        List<Ingrediente> ingredientes = em.createQuery(jpql, Ingrediente.class)
                                            .getResultList();

        assertFalse(ingredientes.isEmpty());
        assertEquals(3, ingredientes.size());
    }

    /**
     * Teste 3 – AVG
     * Média da quantidade de pães associados aos ingredientes
     */
    @Test
    public void testMediaDePaesPorIngrediente() {
        String jpql = """
            SELECT AVG(SIZE(i.paes))
            FROM Ingrediente i
        """;

        Double media = em.createQuery(jpql, Double.class)
                         .getSingleResult();

        assertNotNull(media);
        assertTrue(media > 0);
    }

    /**
     * Teste 4 – BETWEEN
     * Busca ingredientes cujo ID esteja em um intervalo
     */
    @Test
    public void testBuscarIngredientePorIntervaloDeId() {
        String jpql = """
            SELECT i 
            FROM Ingrediente i 
            WHERE i.id BETWEEN 1 AND 3
        """;

        List<Ingrediente> ingredientes = em.createQuery(jpql, Ingrediente.class)
                                            .getResultList();

        assertEquals(3, ingredientes.size());
    }

    /**
     * Teste 5 – SUM
     * Soma total de relações Ingrediente ↔ Pão
     */
    @Test
    public void testSomaTotalDeRelacoesComPao() {
        String jpql = """
            SELECT SUM(SIZE(i.paes))
            FROM Ingrediente i
        """;

        Long soma = em.createQuery(jpql, Long.class)
                      .getSingleResult();

        assertNotNull(soma);
        assertEquals(4, soma);
    }
}