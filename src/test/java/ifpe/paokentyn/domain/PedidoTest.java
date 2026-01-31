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

    //  MÉTODOS AUXILIARES ORIGINAIS 

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

    // JPQL NOVOS

    private Pedido buscarPedidoComItensFetchJPQL(Long id) {
        // 1. JOIN FETCH
        String jpql = "SELECT p FROM Pedido p JOIN FETCH p.itens WHERE p.id = :id";
        TypedQuery<Pedido> query = em.createQuery(jpql, Pedido.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    private List<Object[]> contarItensPorPedidoJPQL(Long minItens) {
        // 2. GROUP BY + HAVING
        String jpql = "SELECT p.id, COUNT(i) FROM Pedido p JOIN p.itens i " +
                      "GROUP BY p.id HAVING COUNT(i) >= :min";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("min", minItens);
        return query.getResultList();
    }

    private List<Pedido> buscarResumoPedidoJPQL() {
        // 3. NEW
        String jpql = "SELECT NEW ifpe.paokentyn.domain.Pedido(p.valorTotal, p.dataPedido) FROM Pedido p";
        return em.createQuery(jpql, Pedido.class).getResultList();
    }

    private List<Pedido> buscarPedidosComDataJPQL() {
        // 4. NOT NULL
        String jpql = "SELECT p FROM Pedido p WHERE p.dataPedido IS NOT NULL";
        return em.createQuery(jpql, Pedido.class).getResultList();
    }

    private Double somarValorTotalPedidosJPQL() {
        // 5. SUM 
        String jpql = "SELECT SUM(p.valorTotal) FROM Pedido p";
        return em.createQuery(jpql, Double.class).getSingleResult();
    }

    // NOVOS TESTES JPQL

    @Test
    public void testBuscarPedidoComItensFetch() {
        logger.info("--- JPQL: JOIN FETCH ---");
        Pedido p = buscarPedidoComItensFetchJPQL(1L);
        assertNotNull(p);
        assertFalse(p.getItens().isEmpty());
        logger.info("Pedido recuperado com {} itens.", p.getItens().size());
    }

    @Test
    public void testContagemItens() {
        logger.info("--- JPQL: GROUP BY + HAVING ---");
        List<Object[]> lista = contarItensPorPedidoJPQL(2L);
        assertFalse(lista.isEmpty());
        logger.info("Pedido agrupado ID: {} - Qtd: {}", lista.get(0)[0], lista.get(0)[1]);
    }

    @Test
    public void testResumoPedidoNew() {
        logger.info("--- JPQL: NEW ---");
        List<Pedido> lista = buscarResumoPedidoJPQL();
        assertFalse(lista.isEmpty());
        assertNull(lista.get(0).getId()); 
        assertNotNull(lista.get(0).getValorTotal());
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
        logger.info("Faturamento Total: {}", total);
    }

    // Testes antigos

    @Test
    public void testEncontrarPedidoDoDataSet() {
        logger.info("--- Executando testEncontrarPedidoDoDataSet ---");
        Pedido pedido = buscarPedidoPorValor(70.00);
        assertNotNull(pedido, "Pedido de valor 70.00 deveria existir no dataset");
        assertEquals(70.00, pedido.getValorTotal());
        logger.info("Pedido encontrado no dataset: id={}, valor={}", pedido.getId(), pedido.getValorTotal());
    }

    @Test
    public void testPersistirPedido() {
        logger.info("--- Executando testPersistirPedido ---");
        Pedido novoPedido = new Pedido();
        novoPedido.setDataPedido(new Date());
        novoPedido.setValorTotal(15.25);
        
        logger.info("Persistindo novo pedido: data={}, valor={}", novoPedido.getDataPedido(), novoPedido.getValorTotal());
        
        em.persist(novoPedido);
        em.flush();
        assertTrue(novoPedido.getId() > 0, "ID deve ser positivo");
        
        Pedido pedidoDataset = buscarPedidoPorValor(70.00);
        assertNotEquals(pedidoDataset.getId(), novoPedido.getId(), "ID não deve ser o mesmo do dataset");
        logger.info("Novo pedido persistido com sucesso: id={}", novoPedido.getId());
    }
    
    @Test
    public void testAtualizarPedidoGerenciado() {
        logger.info("--- Executando testAtualizarPedidoGerenciado (Sem Merge) ---");
        Pedido pedido = buscarPedidoPorValor(70.00);
        assertNotNull(pedido);
        Long idOriginal = pedido.getId();
        Double valorAntigo = pedido.getValorTotal();
        Double novoValor = valorAntigo + 10.0;

        pedido.setValorTotal(novoValor);
        em.flush();
        em.clear();

        Pedido pedidoAtualizado = em.find(Pedido.class, idOriginal);
        assertEquals(novoValor, pedidoAtualizado.getValorTotal());
        logger.info("Valor atualizado via Dirty Checking: {} -> {}", valorAntigo, novoValor);
    }

    @Test
    public void testAtualizarPedidoComMerge() {
        logger.info("--- Executando testAtualizarPedidoComMerge ---");
        Pedido pedido = buscarPedidoPorValor(70.00);
        assertNotNull(pedido);
        Long idOriginal = pedido.getId();
        Double valorAntigo = pedido.getValorTotal();

        em.clear(); 
        pedido.setValorTotal(99.90); 
        em.merge(pedido); 
        em.flush();
        em.clear();

        Pedido pedidoAtualizado = em.find(Pedido.class, idOriginal);
        assertEquals(99.90, pedidoAtualizado.getValorTotal());
        assertNotEquals(valorAntigo, pedidoAtualizado.getValorTotal());
        logger.info("Pedido atualizado: valor mudou de {} para {}", valorAntigo, pedidoAtualizado.getValorTotal());
    }

    @Test
    public void testRemoverPedidoEItens() {
        logger.info("--- Executando testRemoverPedidoEItens ---");
        Pedido pedido = buscarPedidoPorValor(70.00);
        assertNotNull(pedido);
        assertFalse(pedido.getItens().isEmpty(), "O pedido deveria ter itens associados");
        Long idItem1 = pedido.getItens().get(0).getId();
        Long idItem2 = pedido.getItens().size() > 1 ? pedido.getItens().get(1).getId() : null;

        logger.info("Removendo Pedido ID={}. Itens esperados para remoção: {}, {}", pedido.getId(), idItem1, idItem2);
        
        em.remove(pedido); 
        em.flush();
        em.clear();

        Pedido pedidoApagado = buscarPedidoPorValor(70.00);
        assertNull(pedidoApagado, "O pedido deveria ter sido removido");
        assertNull(em.find(ItemPedido.class, idItem1), "O item 1 deveria ter sido removido em cascata");
        if (idItem2 != null) {
            assertNull(em.find(ItemPedido.class, idItem2), "O item 2 deveria ter sido removido em cascata");
        }
        logger.info("Pedido e seus itens removidos com sucesso.");
    }
    
    @Test
    public void testEqualsAndHashCode() {
        logger.info("--- Executando testEqualsAndHashCode ---");
        Pedido p1 = buscarPedidoPorId(2);
        Pedido p2 = buscarPedidoPorId(3);
        Pedido p3 = buscarPedidoPorId(2);
        assertFalse(p1.equals(p2), "Os objetos não devem ser iguais");
        assertTrue(p1.equals(p3), "Os objetos devem ser iguais");
        assertEquals(p1.hashCode(), p3.hashCode(), "Hashcodes devem ser iguais");
    }
}