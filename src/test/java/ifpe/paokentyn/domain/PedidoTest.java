package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
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
        query.setParameter("id", id);
        return query.getResultList().stream().findFirst().orElse(null);
    }
    
    private List<Pedido> buscarPedidosDinamico(Double valorMinimo, Double valorMaximo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pedido> query = cb.createQuery(Pedido.class);
        Root<Pedido> root = query.from(Pedido.class);
        List<Predicate> condicoes = new ArrayList<>();

        if (valorMinimo != null) {
            condicoes.add(cb.ge(root.get("valorTotal"), valorMinimo));
        }
        
        if (valorMaximo != null) {
            condicoes.add(cb.le(root.get("valorTotal"), valorMaximo));
        }

        query.where(cb.and(condicoes.toArray(new Predicate[0])));
        // Ordena pelo maior valor para facilitar o teste
        query.orderBy(cb.desc(root.get("valorTotal"))); 
        return em.createQuery(query).getResultList();
    }


    @Test
    public void testEncontrarPedidoDoDataSet() {
        logger.info("--- Executando testEncontrarPedidoDoDataSet (Dinâmico) ---");

        Pedido pedido = buscarPedidoPorValor(70.00);

        assertNotNull(pedido, "Pedido de valor 70.00 deveria existir no dataset");
        assertEquals(70.00, pedido.getValorTotal());

        logger.info("Pedido encontrado no dataset: id={}, valor={}",
                pedido.getId(), pedido.getValorTotal());
    }

    @Test
    public void testPersistirPedido() {
        logger.info("--- Executando testPersistirPedido ---");

        Pedido novoPedido = new Pedido();
        novoPedido.setDataPedido(new Date());
        novoPedido.setValorTotal(15.25);

        logger.info("Persistindo novo pedido: data={}, valor={}",
                novoPedido.getDataPedido(), novoPedido.getValorTotal());

        em.persist(novoPedido);
        em.flush();

        assertTrue(novoPedido.getId() > 0, "ID deve ser positivo");
        
        Pedido pedidoDataset = buscarPedidoPorValor(70.00);
        assertNotEquals(pedidoDataset.getId(), novoPedido.getId(), "ID não deve ser o mesmo do dataset");

        logger.info("Novo pedido persistido com sucesso: id={}", novoPedido.getId());
    }
    
       @Test
    public void testBuscaDinamicaComCriteria() {
        logger.info("--- Executando testBuscaDinamicaComCriteria (Pedido) ---");

        // Cenário A: Filtrar pedidos caros (>= 60.00)
        // Deve vir apenas o pedido de 70.00
        List<Pedido> caros = buscarPedidosDinamico(60.00, null);
        assertEquals(1, caros.size());
        assertEquals(70.00, caros.get(0).getValorTotal());
        logger.info("Filtro Mínimo OK: Achou pedido de {}", caros.get(0).getValorTotal());

        // Cenário B: Filtrar faixa de preço (entre 50.00 e 80.00)
        // Deve vir os dois (55.00 e 70.00)
        List<Pedido> faixa = buscarPedidosDinamico(50.00, 80.00);
        assertEquals(2, faixa.size());

        // Cenário C: Filtrar valor muito alto (>= 100.00)
        List<Pedido> vips = buscarPedidosDinamico(100.00, null);
        assertTrue(vips.isEmpty());

        // Cenário D: Sem filtros
        List<Pedido> todos = buscarPedidosDinamico(null, null);
        assertEquals(2, todos.size());

        logger.info("Teste de Criteria API finalizado com sucesso.");
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

        logger.info("Pedido atualizado: valor mudou de {} para {}", 
                valorAntigo, pedidoAtualizado.getValorTotal());
    }

    @Test
    public void testRemoverPedidoEItens() {
        logger.info("--- Executando testRemoverPedidoEItens ---");

        Pedido pedido = buscarPedidoPorValor(70.00);
        assertNotNull(pedido);
        
        assertFalse(pedido.getItens().isEmpty(), "O pedido deveria ter itens associados");
        Long idItem1 = pedido.getItens().get(0).getId();
        Long idItem2 = pedido.getItens().size() > 1 ? pedido.getItens().get(1).getId() : null;

        logger.info("Removendo Pedido ID={}. Itens esperados para remoção: {}, {}", 
                pedido.getId(), idItem1, idItem2);

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