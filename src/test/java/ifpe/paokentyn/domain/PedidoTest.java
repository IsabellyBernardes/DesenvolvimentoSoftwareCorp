package ifpe.paokentyn.domain; 
import jakarta.persistence.TypedQuery;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.List;

public class PedidoTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(PedidoTest.class);

    private Pedido buscarPedidoPorValor(Double valor) {
        String jpql = "SELECT p FROM Pedido p WHERE p.valorTotal = :valor";
        TypedQuery<Pedido> query = em.createQuery(jpql, Pedido.class);
        query.setParameter("valor", valor);
        return query.getResultList().stream().findFirst().orElse(null);
    }
    
    private Pedido buscarPedidoPorId(int id) {
        String jpql = "SELECT p FROM Pedido p WHERE p.id = :id";
        TypedQuery<Pedido> query = em.createQuery(jpql, Pedido.class);
        query.setParameter("id", (long) id);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    @Test
    public void testEncontrarPedidoDoDataSet() {
        logger.info("--- Executando testEncontrarPedidoDoDataSet ---");
        Pedido pedido = buscarPedidoPorValor(70.00);
        assertNotNull(pedido);
        assertEquals(70.00, pedido.getValorTotal());
    }

    @Test
    public void testPersistirPedido() {
        logger.info("--- Executando testPersistirPedido ---");
        Pedido novoPedido = new Pedido();
        novoPedido.setDataPedido(new Date());
        novoPedido.setValorTotal(15.25);
        em.persist(novoPedido);
        em.flush();
        assertTrue(novoPedido.getId() > 0);
    }
    
    @Test
    public void testAtualizarPedidoGerenciado() {
        logger.info("--- Executando testAtualizarPedidoGerenciado ---");
        Pedido pedido = buscarPedidoPorValor(70.00);
        Long idOriginal = pedido.getId();
        Double valorAntigo = pedido.getValorTotal();
        Double novoValor = valorAntigo + 10.0;
        pedido.setValorTotal(novoValor);
        em.flush();
        em.clear();
        Pedido pedidoAtualizado = em.find(Pedido.class, idOriginal);
        assertEquals(novoValor, pedidoAtualizado.getValorTotal());
    }

    @Test
    public void testAtualizarPedidoComMerge() {
        logger.info("--- Executando testAtualizarPedidoComMerge ---");
        Pedido pedido = buscarPedidoPorValor(70.00);
        Long idOriginal = pedido.getId();
        em.clear(); 
        pedido.setValorTotal(99.90); 
        em.merge(pedido); 
        em.flush();
        em.clear();
        Pedido pedidoAtualizado = em.find(Pedido.class, idOriginal);
        assertEquals(99.90, pedidoAtualizado.getValorTotal());
    }

    @Test
    public void testRemoverPedidoEItens() {
        logger.info("--- Executando testRemoverPedidoEItens ---");
        Pedido pedido = buscarPedidoPorValor(70.00);
        Long idItem1 = pedido.getItens().get(0).getId();
        em.remove(pedido); 
        em.flush();
        em.clear();
        assertNull(buscarPedidoPorValor(70.00));
        assertNull(em.find(ItemPedido.class, idItem1));
    }
    
    @Test
    public void testEqualsAndHashCode() {
        Pedido p1 = buscarPedidoPorId(2);
        Pedido p3 = buscarPedidoPorId(2);
        assertTrue(p1.equals(p3));
        assertEquals(p1.hashCode(), p3.hashCode());
    }
}