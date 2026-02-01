package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarefaJPQLTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(TarefaJPQLTest.class);

    private Tarefa buscarTarefaComFuncionarioFetchJPQL(Long id) {
        String jpql = "SELECT t FROM Tarefa t JOIN FETCH t.funcionario WHERE t.id = :id";
        TypedQuery<Tarefa> query = em.createQuery(jpql, Tarefa.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    private List<Object[]> contarTarefasPorFuncionarioJPQL() {
        String jpql = "SELECT t.funcionario.nome, COUNT(t) FROM Tarefa t " +
                      "GROUP BY t.funcionario.nome HAVING COUNT(t) >= 1";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    private List<Tarefa> buscarResumoTarefaJPQL() {
        String jpql = "SELECT NEW ifpe.paokentyn.domain.Tarefa(t.descricao, t.dataPrevisao) FROM Tarefa t";
        return em.createQuery(jpql, Tarefa.class).getResultList();
    }

    private List<Tarefa> buscarTarefasPendentesJPQL() {
        String jpql = "SELECT t FROM Tarefa t WHERE t.dataConclusao IS NULL";
        return em.createQuery(jpql, Tarefa.class).getResultList();
    }

    private List<Tarefa> buscarTarefasNaoConcluidasJPQL() {
        String jpql = "SELECT t FROM Tarefa t WHERE t.concluida = false";
        return em.createQuery(jpql, Tarefa.class).getResultList();
    }

    @Test
    public void testJoinFetchFuncionario() {
        logger.info("--- JPQL: JOIN FETCH ---");
        Tarefa t = buscarTarefaComFuncionarioFetchJPQL(1L);
        assertNotNull(t);
        assertNotNull(t.getFuncionario());
    }

    @Test
    public void testGroupTarefas() {
        logger.info("--- JPQL: GROUP BY + HAVING ---");
        List<Object[]> lista = contarTarefasPorFuncionarioJPQL();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testNewResumo() {
        logger.info("--- JPQL: NEW ---");
        List<Tarefa> lista = buscarResumoTarefaJPQL();
        assertFalse(lista.isEmpty());
        assertNull(lista.get(0).getId()); 
    }

    @Test
    public void testIsNull() {
        logger.info("--- JPQL: IS NULL ---");
        List<Tarefa> lista = buscarTarefasPendentesJPQL();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testBooleanCheck() {
        logger.info("--- JPQL: BOOLEAN ---");
        List<Tarefa> lista = buscarTarefasNaoConcluidasJPQL();
        assertFalse(lista.isEmpty());
    }
}