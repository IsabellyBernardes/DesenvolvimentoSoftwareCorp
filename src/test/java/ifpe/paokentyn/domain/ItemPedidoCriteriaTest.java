package ifpe.paokentyn.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemPedidoCriteriaTest extends GenericTest {

    /**
     * Teste 1 – Buscar ItemPedido pelo nome do Pão usando JOIN + Criteria
     */
    @Test
    public void testBuscarItemPedidoPorNomeDoPaoCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ItemPedido> cq = cb.createQuery(ItemPedido.class);
        Root<ItemPedido> root = cq.from(ItemPedido.class);

        Join<ItemPedido, Pao> joinPao = root.join("pao");

        cq.where(cb.equal(joinPao.get("nomePao"), "Pão Integral"));

        ItemPedido item = em.createQuery(cq)
                            .getResultList()
                            .stream()
                            .findFirst()
                            .orElse(null);

        assertNotNull(item);
        assertEquals("Pão Integral", item.getPao().getNomePao());
        assertEquals(10, item.getQuantidade());
    }

    /**
     * Teste 2 – Buscar ItemPedido por quantidade mínima (>=)
     */
    @Test
    public void testBuscarItemPedidoPorQuantidadeMinimaCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ItemPedido> cq = cb.createQuery(ItemPedido.class);
        Root<ItemPedido> root = cq.from(ItemPedido.class);

        cq.where(cb.ge(root.get("quantidade"), 10));

        List<ItemPedido> itens = em.createQuery(cq).getResultList();

        assertEquals(1, itens.size());
        assertEquals(10, itens.get(0).getQuantidade());
    }

    /**
     * Teste 3 – Busca dinâmica com lista de Predicates
     */
    @Test
    public void testBuscaDinamicaComPredicatesCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ItemPedido> cq = cb.createQuery(ItemPedido.class);
        Root<ItemPedido> root = cq.from(ItemPedido.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.ge(root.get("quantidade"), 5));
        predicates.add(cb.le(root.get("quantidade"), 10));

        cq.where(predicates.toArray(new Predicate[0]));

        List<ItemPedido> itens = em.createQuery(cq).getResultList();

        assertEquals(4, itens.size());
    }

    /**
     * Teste 4 – Buscar todos os ItemPedido (Criteria sem WHERE)
     */
    @Test
    public void testBuscarTodosItemPedidoCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ItemPedido> cq = cb.createQuery(ItemPedido.class);
        Root<ItemPedido> root = cq.from(ItemPedido.class);

        cq.select(root);

        List<ItemPedido> itens = em.createQuery(cq).getResultList();

        assertNotNull(itens);
        assertTrue(itens.size() >= 4);
    }

    /**
     * Teste 5 – Buscar ItemPedido por ID usando Criteria
     */
    @Test
    public void testBuscarItemPedidoPorIdCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ItemPedido> cq = cb.createQuery(ItemPedido.class);
        Root<ItemPedido> root = cq.from(ItemPedido.class);

        cq.where(cb.equal(root.get("id"), 2));

        ItemPedido item = em.createQuery(cq)
                            .getResultList()
                            .stream()
                            .findFirst()
                            .orElse(null);

        assertNotNull(item);
        assertEquals(2L, item.getId());
    }
}