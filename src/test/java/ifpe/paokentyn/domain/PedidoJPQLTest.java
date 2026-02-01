package ifpe.paokentyn.domain; 

import jakarta.persistence.TypedQuery;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PedidoJPQLTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(PedidoJPQLTest.class);

    private Pedido buscarPedidoComItensFetchJPQL(Long id) {
        String jpql = "SELECT p FROM Pedido p JOIN FETCH p.itens WHERE p.id = :id";
        TypedQuery<Pedido> query = em.createQuery(jpql, Pedido.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    private List<Object[]> contarItensPorPedidoJPQL(Long minItens) {
        String jpql = "SELECT p.id, COUNT(i) FROM Pedido p JOIN p.itens i GROUP BY p.id HAVING COUNT(i) >= :min";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("min", minItens);
        return query.getResultList();
    }

    private List<Pedido> buscarResumoPedidoJPQL() {
        String jpql = "SELECT NEW ifpe.paokentyn.domain.Pedido(p.valorTotal, p.dataPedido) FROM Pedido p";
        return em.createQuery(jpql, Pedido.class).getResultList();
    }

    private List<Pedido> buscarPedidosComDataJPQL() {
        String jpql = "SELECT p FROM Pedido p WHERE p.dataPedido IS NOT NULL";
        return em.createQuery(jpql, Pedido.class).getResultList();
    }

    private Double somarValorTotalPedidosJPQL() {
        String jpql = "SELECT SUM(p.valorTotal) FROM Pedido p";
        return em.createQuery(jpql, Double.class).getSingleResult();
    }

    @Test
    public void testBuscarPedidoComItensFetch() {
        logger.info("--- JPQL: JOIN FETCH ---");
        Pedido p = buscarPedidoComItensFetchJPQL(1L);
        assertNotNull(p);
        assertFalse(p.getItens().isEmpty());
    }

    @Test
    public void testContagemItens() {
        logger.info("--- JPQL: GROUP BY + HAVING ---");
        List<Object[]> lista = contarItensPorPedidoJPQL(2L);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testResumoPedidoNew() {
        logger.info("--- JPQL: NEW ---");
        List<Pedido> lista = buscarResumoPedidoJPQL();
        assertFalse(lista.isEmpty());
        assertNull(lista.get(0).getId()); 
    }

    @Test
    public void testDataNaoNula() {
        logger.info("--- JPQL: IS NOT NULL ---");
        List<Pedido> lista = buscarPedidosComDataJPQL();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testSomaTotal() {
        logger.info("--- JPQL: SUM ---");
        Double total = somarValorTotalPedidosJPQL();
        assertTrue(total > 0);
    }
}