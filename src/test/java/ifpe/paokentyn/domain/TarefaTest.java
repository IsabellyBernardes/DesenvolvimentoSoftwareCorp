package ifpe.paokentyn.domain; 

import jakarta.persistence.TypedQuery;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class TarefaTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(TarefaTest.class);

    private Tarefa buscarTarefaPorDescricao(String descricao) {
        String jpql = "SELECT t FROM Tarefa t WHERE t.descricao = :descricao";
        TypedQuery<Tarefa> query = em.createQuery(jpql, Tarefa.class);
        query.setParameter("descricao", descricao);
        return query.getSingleResult();
    }
    
    private Tarefa buscarTarefaPorId(int id) {
        String jpql = "SELECT t FROM Tarefa t WHERE t.id = :id";
        TypedQuery<Tarefa> query = em.createQuery(jpql, Tarefa.class);
        query.setParameter("id", (long) id);
        return query.getSingleResult();
    }

    private Funcionario buscarFuncionarioPorNome(String nome) {
        String jpql = "SELECT f FROM Funcionario f WHERE f.nome = :nome";
        TypedQuery<Funcionario> query = em.createQuery(jpql, Funcionario.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }

    @Test
    public void testEncontrarTarefaDoDataSet() {
        logger.info("Executando testEncontrarTarefaDoDataSet");
        Tarefa tarefa = buscarTarefaPorDescricao("Checar estoque de farinha");
        assertNotNull(tarefa);
        assertEquals("Checar estoque de farinha", tarefa.getDescricao());
    }

    @Test
    public void testPersistirTarefa() {
        logger.info("Executando testPersistirTarefa");
        Funcionario func = buscarFuncionarioPorNome("João Silva");
        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setDescricao("Limpar forno");
        novaTarefa.setDataInicio(new Date());
        novaTarefa.setDataPrevisao(new Date(System.currentTimeMillis() + 86400000));
        novaTarefa.setConcluida(false);
        novaTarefa.setFuncionario(func);

        em.persist(novaTarefa); 
        em.flush(); 
        assertNotNull(novaTarefa.getId());
    }
    
    @Test
    public void testAtualizarTarefaGerenciada() {
        logger.info("--- Executando testAtualizarTarefaGerenciada ---");
        Tarefa tarefa = buscarTarefaPorDescricao("Checar estoque de farinha");
        Long idOriginal = tarefa.getId();
        tarefa.setDescricao("Verificar validade da farinha");
        em.flush(); 
        em.clear();
        Tarefa tarefaAtualizada = em.find(Tarefa.class, idOriginal);
        assertEquals("Verificar validade da farinha", tarefaAtualizada.getDescricao());
    }

    @Test
    public void testAtualizarTarefaComMerge() {
        logger.info("Executando testAtualizarTarefaComMerge");
        Tarefa tarefa = buscarTarefaPorDescricao("Checar estoque de farinha");
        Long idOriginal = tarefa.getId();
        em.clear(); 
        tarefa.setConcluida(true); 
        em.merge(tarefa); 
        em.flush();
        em.clear();
        Tarefa tarefaAtualizada = em.find(Tarefa.class, idOriginal);
        assertTrue(tarefaAtualizada.getConcluida());
    }

    @Test
    public void testRemoverTarefa() {
        logger.info("Executando testRemoverTarefa");
        Tarefa tarefa = buscarTarefaPorDescricao("Checar estoque de farinha");
        Long idFuncionario = tarefa.getFuncionario().getId();
        em.remove(tarefa); 
        em.flush();
        em.clear();
        
        String jpqlCheck = "SELECT t FROM Tarefa t WHERE t.descricao = :desc";
        List<Tarefa> lista = em.createQuery(jpqlCheck, Tarefa.class)
                              .setParameter("desc", "Checar estoque de farinha")
                              .getResultList();
        assertTrue(lista.isEmpty());
        assertNotNull(em.find(Funcionario.class, idFuncionario));
    }
    
    @Test
    public void testEqualsAndHashCode() {
        Tarefa t1 = buscarTarefaPorId(2);
        Tarefa t3 = buscarTarefaPorId(2);
        assertTrue(t1.equals(t3));
        assertEquals(t1.hashCode(), t3.hashCode());
    }
}