package ifpe.paokentyn.domain;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

public class PedidoCriteriaTest extends GenericTest {
    
    private static final Logger logger = LoggerFactory.getLogger(PedidoCriteriaTest.class);

    // Movidos do original
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
        query.orderBy(cb.desc(root.get("valorTotal"))); 
        return em.createQuery(query).getResultList();
    }

    // Movidos do original
    @Test
    public void testBuscaDinamicaComCriteria() {
        logger.info("--- Executando testBuscaDinamicaComCriteria (Original Movido) ---");

        
        List<Pedido> caros = buscarPedidosDinamico(60.00, null);
        assertEquals(1, caros.size());
        assertEquals(70.00, caros.get(0).getValorTotal());

        
        List<Pedido> faixa = buscarPedidosDinamico(50.00, 80.00);
        assertEquals(2, faixa.size());

       
        List<Pedido> vips = buscarPedidosDinamico(100.00, null);
        assertTrue(vips.isEmpty());

       
        List<Pedido> todos = buscarPedidosDinamico(null, null);
        assertEquals(2, todos.size());
    }

    //CRITERIA 

    @Test
    public void testCriteriaJoinFetch() {
        logger.info("--- Criteria: JOIN FETCH ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pedido> c = cb.createQuery(Pedido.class);
        Root<Pedido> root = c.from(Pedido.class);
        
        root.fetch("itens", JoinType.LEFT);
        
        c.where(cb.equal(root.get("id"), 1L)); 
        
        // getResultList() para evitar erro com OneToMany
        List<Pedido> resultados = em.createQuery(c).getResultList();
        
        assertFalse(resultados.isEmpty());
        Pedido p = resultados.get(0);
        
        assertFalse(p.getItens().isEmpty());
    }

    @Test
    public void testCriteriaGroupBy() {
        logger.info("--- Criteria: GROUP BY + HAVING ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> c = cb.createQuery(Object[].class);
        Root<Pedido> root = c.from(Pedido.class);
        
        Join<Pedido, ItemPedido> join = root.join("itens");
        
        c.multiselect(root.get("id"), cb.count(join));
        c.groupBy(root.get("id"));
        
        c.having(cb.ge(cb.count(join), 2L));
        
        List<Object[]> lista = em.createQuery(c).getResultList();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testCriteriaNew() {
        logger.info("--- Criteria: NEW (Construct) ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pedido> c = cb.createQuery(Pedido.class);
        Root<Pedido> root = c.from(Pedido.class);
        
        c.select(cb.construct(Pedido.class, root.get("valorTotal"), root.get("dataPedido")));
        
        List<Pedido> lista = em.createQuery(c).getResultList();
        assertFalse(lista.isEmpty());
        assertNull(lista.get(0).getId()); 
    }

    @Test
    public void testCriteriaIsNotNull() {
        logger.info("--- Criteria: IS NOT NULL ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pedido> c = cb.createQuery(Pedido.class);
        Root<Pedido> root = c.from(Pedido.class);
        
        c.where(cb.isNotNull(root.get("dataPedido")));
        
        List<Pedido> lista = em.createQuery(c).getResultList();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testCriteriaValorMaiorQue() {
        logger.info("--- Criteria: Valor Maior Que (GT) ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pedido> c = cb.createQuery(Pedido.class);
        Root<Pedido> root = c.from(Pedido.class);
        
        c.where(cb.gt(root.get("valorTotal"), 60.0)); 
        
        List<Pedido> lista = em.createQuery(c).getResultList();
        assertEquals(1, lista.size()); 
    }
}