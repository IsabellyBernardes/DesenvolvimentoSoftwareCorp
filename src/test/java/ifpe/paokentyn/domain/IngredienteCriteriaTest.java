package ifpe.paokentyn.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IngredienteCriteriaTest extends GenericTest {

    /**
     * Teste 1 – Buscar ingrediente por nome exato usando Criteria
     */
    @Test
    public void testBuscarIngredientePorNomeCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ingrediente> cq = cb.createQuery(Ingrediente.class);
        Root<Ingrediente> root = cq.from(Ingrediente.class);

        cq.where(cb.equal(root.get("nome"), "Ovos"));

        Ingrediente ingrediente = em.createQuery(cq).getSingleResult();

        assertNotNull(ingrediente);
        assertEquals("Ovos", ingrediente.getNome());
    }

    /**
     * Teste 2 – Busca dinâmica com LIKE (nome contendo termo)
     */
    @Test
    public void testBuscarIngredientePorLikeCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ingrediente> cq = cb.createQuery(Ingrediente.class);
        Root<Ingrediente> root = cq.from(Ingrediente.class);

        cq.where(
            cb.like(
                cb.lower(root.get("nome")),
                "%trigo%"
            )
        );

        List<Ingrediente> resultados = em.createQuery(cq).getResultList();

        assertEquals(1, resultados.size());
        assertEquals("Farinha de Trigo", resultados.get(0).getNome());
    }

    /**
     * Teste 3 – Busca dinâmica com múltiplos critérios (lista de Predicates)
     */
    @Test
    public void testBuscaComPredicatesDinamicos() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ingrediente> cq = cb.createQuery(Ingrediente.class);
        Root<Ingrediente> root = cq.from(Ingrediente.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(
            cb.like(cb.lower(root.get("nome")), "%o%")
        );

        cq.where(predicates.toArray(new Predicate[0]));

        List<Ingrediente> ingredientes = em.createQuery(cq).getResultList();

        assertFalse(ingredientes.isEmpty());
        assertEquals(3, ingredientes.size());
    }

    /**
     * Teste 4 – Busca sem filtros (Criteria sem WHERE)
     */
    @Test
    public void testBuscarTodosIngredientesCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ingrediente> cq = cb.createQuery(Ingrediente.class);
        Root<Ingrediente> root = cq.from(Ingrediente.class);

        cq.select(root);

        List<Ingrediente> ingredientes = em.createQuery(cq).getResultList();

        assertNotNull(ingredientes);
        assertTrue(ingredientes.size() >= 3);
    }

    /**
     * Teste 5 – Buscar ingrediente por ID usando Criteria
     */
    @Test
    public void testBuscarIngredientePorIdCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ingrediente> cq = cb.createQuery(Ingrediente.class);
        Root<Ingrediente> root = cq.from(Ingrediente.class);

        cq.where(cb.equal(root.get("id"), 2));

        Ingrediente ingrediente = em.createQuery(cq).getSingleResult();

        assertNotNull(ingrediente);
        assertEquals(2L, ingrediente.getId());
    }
}