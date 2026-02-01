package ifpe.paokentyn.domain;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

public class TarefaCriteriaTest extends GenericTest {
    
    private static final Logger logger = LoggerFactory.getLogger(TarefaCriteriaTest.class);

    private List<Tarefa> buscarTarefasDinamico(String parteDescricao, Boolean concluida) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tarefa> query = cb.createQuery(Tarefa.class);
        Root<Tarefa> root = query.from(Tarefa.class);
        List<Predicate> condicoes = new ArrayList<>();

        if (parteDescricao != null && !parteDescricao.isEmpty()) {
            condicoes.add(cb.like(cb.lower(root.get("descricao")), "%" + parteDescricao.toLowerCase() + "%"));
        }

        if (concluida != null) {
            if (concluida) {
                condicoes.add(cb.isTrue(root.get("concluida")));
            } else {
                condicoes.add(cb.isFalse(root.get("concluida")));
            }
        }

        query.where(cb.and(condicoes.toArray(new Predicate[0])));
        return em.createQuery(query).getResultList();
    }

    @Test
    public void testBuscaDinamicaComCriteria() {
        logger.info("--- Executando testBuscaDinamicaComCriteria (Original Movido) ---");

       
        List<Tarefa> tarefasEstoque = buscarTarefasDinamico("estoque", null);
        assertEquals(1, tarefasEstoque.size());
        assertTrue(tarefasEstoque.get(0).getDescricao().contains("estoque"));

        
        List<Tarefa> pendentes = buscarTarefasDinamico(null, false);
        assertEquals(3, pendentes.size());

       
        List<Tarefa> concluidas = buscarTarefasDinamico(null, true);
        assertTrue(concluidas.isEmpty());

        
        List<Tarefa> assar = buscarTarefasDinamico("pães", false);
        assertEquals(1, assar.size());
        assertEquals("Assar pães", assar.get(0).getDescricao());
    }

    @Test
    public void testCriteriaJoinFetch() {
        logger.info("--- Criteria: JOIN FETCH ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tarefa> c = cb.createQuery(Tarefa.class);
        Root<Tarefa> root = c.from(Tarefa.class);
        
  
        root.fetch("funcionario", JoinType.LEFT);
        c.where(cb.equal(root.get("id"), 1L));
        
        List<Tarefa> resultados = em.createQuery(c).getResultList();
        assertFalse(resultados.isEmpty());
        assertNotNull(resultados.get(0).getFuncionario());
    }

    @Test
    public void testCriteriaGroupBy() {
        logger.info("--- Criteria: GROUP BY ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> c = cb.createQuery(Object[].class);
        Root<Tarefa> root = c.from(Tarefa.class);
        
        c.multiselect(root.get("funcionario"), cb.count(root));
        c.groupBy(root.get("funcionario"));
        c.having(cb.ge(cb.count(root), 1L));
        
        List<Object[]> lista = em.createQuery(c).getResultList();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testCriteriaNew() {
        logger.info("--- Criteria: NEW ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tarefa> c = cb.createQuery(Tarefa.class);
        Root<Tarefa> root = c.from(Tarefa.class);
        
        c.select(cb.construct(Tarefa.class, root.get("descricao"), root.get("dataPrevisao")));
        
        List<Tarefa> lista = em.createQuery(c).getResultList();
        assertFalse(lista.isEmpty());
        assertNull(lista.get(0).getId()); 
    }

    @Test
    public void testCriteriaIsNull() {
        logger.info("--- Criteria: IS NULL ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tarefa> c = cb.createQuery(Tarefa.class);
        Root<Tarefa> root = c.from(Tarefa.class);
        
        c.where(cb.isNull(root.get("dataConclusao")));
        
        List<Tarefa> lista = em.createQuery(c).getResultList();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testCriteriaOrdenacao() {
        logger.info("--- Criteria: ORDER BY (Livre) ---");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tarefa> c = cb.createQuery(Tarefa.class);
        Root<Tarefa> root = c.from(Tarefa.class);
        
        c.orderBy(cb.asc(root.get("dataPrevisao")));
        
        List<Tarefa> lista = em.createQuery(c).getResultList();
        assertFalse(lista.isEmpty());
        if (lista.size() > 1) {
            assertTrue(lista.get(0).getDataPrevisao().compareTo(lista.get(lista.size()-1).getDataPrevisao()) <= 0);
        }
    }
}