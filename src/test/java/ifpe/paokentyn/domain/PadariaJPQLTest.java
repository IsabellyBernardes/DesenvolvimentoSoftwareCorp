package ifpe.paokentyn.domain;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PadariaJPQLTest extends GenericTest {

    /**
     * Teste 1 – Manipulação de String com LIKE
     * Busca padarias que contenham "Melhor Teste" no nome
     */
    @Test
    public void testBuscarPadariaPorNomeLike() {
        String jpql = """
            SELECT p
            FROM Padaria p
            WHERE LOWER(p.nome) LIKE '%melhor teste%'
        """;

        List<Padaria> padarias = em.createQuery(jpql, Padaria.class)
                                   .getResultList();

        assertEquals(3, padarias.size());
    }

    /**
     * Teste 2 – Manipulação de String com LENGTH
     * Busca padarias cujo nome tenha mais de 25 caracteres
     */
    @Test
    public void testBuscarPadariaPorTamanhoDoNome() {
        String jpql = """
            SELECT p
            FROM Padaria p
            WHERE LENGTH(p.nome) > 20
        """;

        List<Padaria> padarias = em.createQuery(jpql, Padaria.class)
                                   .getResultList();

        assertEquals(3, padarias.size());
    }

    /**
     * Teste 3 – AVG
     * Média de funcionários por padaria
     */
    @Test
    public void testMediaFuncionariosPorPadaria() {
        String jpql = """
            SELECT AVG(SIZE(p.funcionarios))
            FROM Padaria p
        """;

        Double media = em.createQuery(jpql, Double.class)
                         .getSingleResult();

        assertNotNull(media);
        assertEquals(1.0, media, 0.01);
    }

    /**
     * Teste 4 – BETWEEN
     * Busca padarias cujo ID esteja em um intervalo
     */
    @Test
    public void testBuscarPadariaPorIntervaloDeId() {
        String jpql = """
            SELECT p
            FROM Padaria p
            WHERE p.id BETWEEN 2 AND 3
        """;

        List<Padaria> padarias = em.createQuery(jpql, Padaria.class)
                                   .getResultList();

        assertEquals(2, padarias.size());
    }

    /**
     * Teste 5 – SUM
     * Soma total de fornadas associadas às padarias
     */
    @Test
    public void testSomaTotalFornadasDasPadarias() {
        String jpql = """
            SELECT SUM(SIZE(p.fornadas))
            FROM Padaria p
        """;

        Long soma = em.createQuery(jpql, Long.class)
                      .getSingleResult();

        assertNotNull(soma);
        assertEquals(3, soma);
    }
}