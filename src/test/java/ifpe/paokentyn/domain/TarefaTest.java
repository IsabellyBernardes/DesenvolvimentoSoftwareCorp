package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        query.setParameter("id", id);
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
        logger.info("Executando testEncontrarTarefaDoDataSet (Dinâmico)");

        Tarefa tarefa = buscarTarefaPorDescricao("Checar estoque de farinha");
        logger.debug("Tarefa buscada: {}", tarefa);

        assertNotNull(tarefa, "Deveria existir a tarefa de checar estoque");
        assertEquals("Checar estoque de farinha", tarefa.getDescricao());
        assertEquals(false, tarefa.getConcluida());

        assertNotNull(tarefa.getFuncionario(), "O funcionário da tarefa não deveria ser nulo");
        assertEquals("João Silva", tarefa.getFuncionario().getNome());
        
        assertNotNull(tarefa.getFuncionario().getPadaria(), "A padaria do funcionário não deveria ser nula");
        assertEquals("Padaria do Melhor Teste", tarefa.getFuncionario().getPadaria().getNome());
        
        logger.info("Tarefa encontrada: '{}' para o funcionário {}", 
                    tarefa.getDescricao(), tarefa.getFuncionario().getNome());
    }

    @Test
    public void testPersistirTarefa() {
        logger.info("Executando testPersistirTarefa");

        Funcionario funcExistente = buscarFuncionarioPorNome("João Silva");
        assertNotNull(funcExistente, "Funcionário do dataset não encontrado");

        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setDescricao("Limpar forno");
        novaTarefa.setDataInicio(new Date());
        novaTarefa.setDataPrevisao(new Date(System.currentTimeMillis() + 86400000));
        novaTarefa.setConcluida(false);

        novaTarefa.setFuncionario(funcExistente);

        em.persist(novaTarefa); 
        em.flush(); 

        assertNotNull(novaTarefa.getId(), "ID da nova tarefa não pode ser nulo");
        assertTrue(novaTarefa.getId() > 0, "ID deve ser positivo");
        
        Tarefa tarefaDoDataset = buscarTarefaPorDescricao("Checar estoque de farinha");
        assertNotEquals(tarefaDoDataset.getId(), novaTarefa.getId());

        logger.info("Tarefa persistida: '{}' com ID: {}", 
                    novaTarefa.getDescricao(), novaTarefa.getId());
    }
    
    @Test
    public void testAtualizarTarefaGerenciada() {
        logger.info("--- Executando testAtualizarTarefaGerenciada (Sem Merge) ---");

        Tarefa tarefa = buscarTarefaPorDescricao("Checar estoque de farinha");
        assertNotNull(tarefa);
        Long idOriginal = tarefa.getId();
        
        String novaDescricao = "Verificar validade da farinha";

        tarefa.setDescricao(novaDescricao);

        em.flush(); 
        em.clear();

        Tarefa tarefaAtualizada = em.find(Tarefa.class, idOriginal);
        assertEquals(novaDescricao, tarefaAtualizada.getDescricao());
        
        logger.info("Tarefa atualizada automaticamente via Dirty Checking.");
    }

    @Test
    public void testAtualizarTarefaComMerge() {
        logger.info("Executando testAtualizarTarefaComMerge");

        Tarefa tarefa = buscarTarefaPorDescricao("Checar estoque de farinha");
        assertNotNull(tarefa);
        Long idOriginal = tarefa.getId();
        
        em.clear(); 

        tarefa.setConcluida(true); 
        tarefa.setDescricao("Estoque verificado e atualizado");

        em.merge(tarefa); 

        em.flush();
        em.clear();

        Tarefa tarefaAtualizada = em.find(Tarefa.class, idOriginal);
        assertTrue(tarefaAtualizada.getConcluida(), "A tarefa deveria estar concluída");
        assertEquals("Estoque verificado e atualizado", tarefaAtualizada.getDescricao());

        logger.info("Tarefa atualizada: Concluída={}, Descrição='{}'", 
                tarefaAtualizada.getConcluida(), tarefaAtualizada.getDescricao());
    }

    @Test
    public void testRemoverTarefa() {
        logger.info("Executando testRemoverTarefa");

        Tarefa tarefa = buscarTarefaPorDescricao("Checar estoque de farinha");
        assertNotNull(tarefa);
        
        Long idFuncionario = tarefa.getFuncionario().getId();

        em.remove(tarefa); 

        em.flush();
        em.clear();

        String jpqlCheck = "SELECT t FROM Tarefa t WHERE t.descricao = :desc";
        var lista = em.createQuery(jpqlCheck, Tarefa.class)
                      .setParameter("desc", "Checar estoque de farinha")
                      .getResultList();
        assertTrue(lista.isEmpty(), "A tarefa deveria ter sido removida");
        
        assertNotNull(em.find(Funcionario.class, idFuncionario), "O funcionário NÃO deve ser removido");

        logger.info("Tarefa removida com sucesso.");
    }
    
    @Test
    public void testEqualsAndHashCode() {
        logger.info("--- Executando testEqualsAndHashCode ---");

        Tarefa t1 = buscarTarefaPorId(2);
        Tarefa t2 = buscarTarefaPorId(3);
        Tarefa t3 = buscarTarefaPorId(2);

        assertFalse(t1.equals(t2), "Os objetos não devem ser iguais");
        assertTrue(t1.equals(t3), "Os objetos devem ser iguais");
        assertEquals(t1.hashCode(), t3.hashCode(), "Hashcodes devem ser iguais");
    }
}