package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaoJPQLTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(PaoJPQLTest.class);

    // --- MÉTODOS AUXILIARES JPQL ---

    private Pao buscarPaoComIngredientesFetchJPQL(Long id) {
        String jpql = "SELECT p FROM Pao p JOIN FETCH p.ingredientes WHERE p.id = :id";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    private List<Object[]> listarPaesComMuitosIngredientesJPQL(Long qtdMinima) {
        String jpql = "SELECT p.nomePao, COUNT(i) FROM Pao p JOIN p.ingredientes i " +
                      "GROUP BY p.nomePao HAVING COUNT(i) >= :qtd";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("qtd", qtdMinima);
        return query.getResultList();
    }

    private List<Pao> buscarResumoPaoJPQL(Double precoMaximo) {
        String jpql = "SELECT NEW ifpe.paokentyn.domain.Pao(p.nomePao, p.preco) " + 
                      "FROM Pao p WHERE p.preco < :max";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("max", precoMaximo);
        return query.getResultList();
    }

    private List<Pao> buscarPaesComImagemJPQL() {
        String jpql = "SELECT p FROM Pao p WHERE p.imagem IS NOT NULL";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        return query.getResultList();
    }

    private List<Pao> buscarPaesPorFaixaPrecoJPQL(Double min, Double max) {
        String jpql = "SELECT p FROM Pao p WHERE p.preco BETWEEN :min AND :max ORDER BY p.preco ASC";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("min", min);
        query.setParameter("max", max);
        return query.getResultList();
    }

    // --- TESTES JPQL ---

    @Test
    public void testBuscarPaoComIngredientesFetch() {
        logger.info("--- JPQL: JOIN FETCH ---");
        Pao pao = buscarPaoComIngredientesFetchJPQL(2L);
        assertNotNull(pao);
        assertFalse(pao.getIngredientes().isEmpty());
    }

    @Test
    public void testAgrupamentoIngredientes() {
        logger.info("--- JPQL: GROUP BY + HAVING ---");
        List<Object[]> lista = listarPaesComMuitosIngredientesJPQL(2L);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testRetornoComNew() {
        logger.info("--- JPQL: NEW ---");
        List<Pao> lista = buscarResumoPaoJPQL(20.0);
        assertFalse(lista.isEmpty());
        assertNull(lista.get(0).getId()); 
    }

    @Test
    public void testImagemNaoNula() {
        logger.info("--- JPQL: IS NOT NULL ---");
        List<Pao> lista = buscarPaesComImagemJPQL();
        assertNotNull(lista); 
    }

    @Test
    public void testFaixaDePreco() {
        logger.info("--- JPQL: BETWEEN ---");
        List<Pao> lista = buscarPaesPorFaixaPrecoJPQL(1.0, 10.0);
        assertFalse(lista.isEmpty());
    }
}