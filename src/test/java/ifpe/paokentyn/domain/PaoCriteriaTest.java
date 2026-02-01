package ifpe.paokentyn.domain;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

public class PaoCriteriaTest extends GenericTest {
    
    private static final Logger logger = LoggerFactory.getLogger(PaoCriteriaTest.class);

    private List<Pao> buscarPaesComFiltroDinamico(String parteNome, Double precoMaximo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pao> query = cb.createQuery(Pao.class);
        Root<Pao> root = query.from(Pao.class);
        List<Predicate> condicoes = new ArrayList<>();

        if (parteNome != null && !parteNome.isEmpty()) {
            condicoes.add(cb.like(cb.lower(root.get("nomePao")), "%" + parteNome.toLowerCase() + "%"));
        }
        if (precoMaximo != null) {
            condicoes.add(cb.le(root.get("preco"), precoMaximo));
        }
        query.where(cb.and(condicoes.toArray(new Predicate[0])));
        return em.createQuery(query).getResultList();
    }

    @Test
    public void testBuscaDinamicaComCriteria() {
        logger.info("--- Executando testBuscaDinamicaComCriteria (Original) ---");
        List<Pao> paesBaratos = buscarPaesComFiltroDinamico(null, 4.00);
        assertEquals(2, paesBaratos.size(), "Deveria vir 2 pães baratos (Queijo e Sal)");
        assertTrue(paesBaratos.stream().allMatch(p -> p.getPreco() <= 4.00));

        List<Pao> paesIntegrais = buscarPaesComFiltroDinamico("Integral", null);
        assertEquals(1, paesIntegrais.size());
        assertEquals("Pão Integral", paesIntegrais.get(0).getNomePao());

        List<Pao> paesImpossiveis = buscarPaesComFiltroDinamico("Queijo", 1.00);
        assertTrue(paesImpossiveis.isEmpty(), "Não deveria achar nada com esses filtros restritos");

        List<Pao> todos = buscarPaesComFiltroDinamico(null, null);
        assertEquals(4, todos.size(), "Deveria trazer todos os 4 pães do dataset");
    }

   @Test
    public void testCriteriaJoinFetch() {
        logger.info("--- Criteria: JOIN FETCH ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pao> c = cb.createQuery(Pao.class);
        Root<Pao> root = c.from(Pao.class);
     
        root.fetch("ingredientes", JoinType.LEFT);
        c.where(cb.equal(root.get("id"), 2L)); 
        List<Pao> resultados = em.createQuery(c).getResultList();
        
        assertFalse(resultados.isEmpty());
        Pao pao = resultados.get(0); 
        assertNotNull(pao);
        assertFalse(pao.getIngredientes().isEmpty());
    }

    @Test
    public void testCriteriaGroupBy() {
        logger.info("--- Criteria: GROUP BY + HAVING ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> c = cb.createQuery(Object[].class);
        Root<Pao> root = c.from(Pao.class);
        
        Join<Pao, Ingrediente> join = root.join("ingredientes");

        c.multiselect(root.get("nomePao"), cb.count(join));
        c.groupBy(root.get("nomePao"));
        c.having(cb.ge(cb.count(join), 2L));

        List<Object[]> lista = em.createQuery(c).getResultList();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testCriteriaNew() {
        logger.info("--- Criteria: NEW (Construct) ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pao> c = cb.createQuery(Pao.class);
        Root<Pao> root = c.from(Pao.class);

        c.select(cb.construct(Pao.class, root.get("nomePao"), root.get("preco")));
        c.where(cb.lt(root.get("preco"), 20.0));

        List<Pao> lista = em.createQuery(c).getResultList();
        assertFalse(lista.isEmpty());
        assertNull(lista.get(0).getId()); 
    }

    @Test
    public void testCriteriaIsNotNull() {
        logger.info("--- Criteria: IS NOT NULL ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pao> c = cb.createQuery(Pao.class);
        Root<Pao> root = c.from(Pao.class);

        c.where(cb.isNotNull(root.get("imagem")));

        List<Pao> lista = em.createQuery(c).getResultList();
        assertNotNull(lista);
    }

    @Test
    public void testCriteriaBetween() {
        logger.info("--- Criteria: BETWEEN ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pao> c = cb.createQuery(Pao.class);
        Root<Pao> root = c.from(Pao.class);

        c.where(cb.between(root.get("preco"), 1.0, 10.0));
        c.orderBy(cb.asc(root.get("preco")));

        List<Pao> lista = em.createQuery(c).getResultList();
        assertFalse(lista.isEmpty());
    }
}