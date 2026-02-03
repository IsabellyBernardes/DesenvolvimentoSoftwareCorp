package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(FuncionarioTest.class);

    private Funcionario buscarFuncionarioPorNomeJPQL(String nome) {
        String jpql = "SELECT f FROM Funcionario f WHERE f.nome = :nome";
        TypedQuery<Funcionario> query = em.createQuery(jpql, Funcionario.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }

    private Funcionario buscarFuncionarioPorIdJPQL(long id) {
        String jpql = "SELECT f FROM Funcionario f WHERE f.id = :id";
        TypedQuery<Funcionario> query = em.createQuery(jpql, Funcionario.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    private Padaria buscarPadariaPorNomeJPQL(String nome) {
        String jpql = "SELECT p FROM Padaria p WHERE p.nome = :nome";
        TypedQuery<Padaria> query = em.createQuery(jpql, Padaria.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }
    
    private List<Funcionario> buscarFuncionariosPorCargoJPQL(String cargo) {
        String jpql = "SELECT f FROM Funcionario f WHERE f.cargo = :cargo";
        TypedQuery<Funcionario> query = em.createQuery(jpql, Funcionario.class);
        query.setParameter("cargo", cargo);
        return query.getResultList();
    }

    private Long contarFuncionariosDaPadariaJPQL(String nomePadaria) {
        String jpql = "SELECT COUNT(f) FROM Funcionario f WHERE f.padaria.nome = :nome";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("nome", nomePadaria);
        return query.getSingleResult();
    }

    @Test
    public void testEncontrarFuncionarioDoDataSet() {
        logger.info("--- Executando testEncontrarFuncionarioDoDataSet ---");

        Funcionario func = buscarFuncionarioPorNomeJPQL("João Silva");

        assertNotNull(func, "Funcionário João Silva deveria existir no dataset");
        assertEquals("Padeiro Senior", func.getCargo());

        assertNotNull(func.getPadaria());
        assertEquals("Padaria do Melhor Teste", func.getPadaria().getNome());

        assertNotNull(func.getDadosBancarios(), "Os DadosBancarios não deveriam ser nulos");
        assertEquals("12345-6", func.getDadosBancarios().getConta());

        logger.info("Encontrado funcionário {} (ID={})", func.getNome(), func.getId());
    }

    @Test
    public void testPersistirFuncionarioComDadosBancarios() {
        logger.info("--- Executando testPersistirFuncionarioComDadosBancarios ---");
        Padaria padaria = em.find(Padaria.class, 1L);

        Funcionario novoFunc = new Funcionario();
        novoFunc.setPadaria(padaria);
        novoFunc.setNome("Carlos Silva");
        novoFunc.setCargo("Caixa");
        novoFunc.setSalario(2000.0);
        novoFunc.setDataContratacao(new java.util.Date());
        
        novoFunc.setCpf("64623918041"); 
        novoFunc.setEmail("carlos@padaria.com"); 

        em.persist(novoFunc);
        em.flush();

        assertNotNull(novoFunc.getId(), "O funcionário deveria ser persistido com sucesso");
    }

    @Test
    public void testAtualizarFuncionarioGerenciado() {
        logger.info("--- Executando testAtualizarFuncionarioGerenciado (Sem Merge) ---");

        Funcionario func = buscarFuncionarioPorNomeJPQL("João Silva");
        assertNotNull(func);
        Long idOriginal = func.getId();
        
        func.setCargo("Mestre Padeiro"); 
        
        em.flush(); 
        em.clear();
       
        Funcionario funcAtualizado = em.find(Funcionario.class, idOriginal);
        assertEquals("Mestre Padeiro", funcAtualizado.getCargo());
        
        logger.info("Cargo atualizado automaticamente via Dirty Checking.");
    }
    
    @Test
    public void testAtualizarFuncionarioComMerge() {
        logger.info("--- Executando testAtualizarFuncionarioComMerge ---");
        
        Funcionario func = buscarFuncionarioPorNomeJPQL("João Silva");
        assertNotNull(func);
        Long idOriginal = func.getId();
        
        em.clear(); 
        
        func.setCargo("Gerente de Produção");
        func.setSalario(4500.00);
        
        em.merge(func); 
        
        em.flush();
        em.clear();
        
        Funcionario funcAtualizado = em.find(Funcionario.class, idOriginal);
        assertEquals("Gerente de Produção", funcAtualizado.getCargo());
        assertEquals(4500.00, funcAtualizado.getSalario());
        
        logger.info("Funcionário atualizado com sucesso.");
    }

    @Test
    public void testRemoverFuncionarioECascade() {
        logger.info("--- Executando testRemoverFuncionarioECascade ---");
        
        Funcionario func = buscarFuncionarioPorNomeJPQL("João Silva");
        assertNotNull(func);
        
        assertNotNull(func.getDadosBancarios());
        Long idDadosBancarios = func.getDadosBancarios().getId();
        
        assertFalse(func.getTarefas().isEmpty());
        Long idTarefa = func.getTarefas().get(0).getId();
        
        logger.info("Removendo funcionário ID={}. Filhos: Dados={}, Tarefa={}", 
                func.getId(), idDadosBancarios, idTarefa);
        
        em.remove(func); 
        
        em.flush();
        em.clear();
        
        String jpqlCheck = "SELECT f FROM Funcionario f WHERE f.nome = :nome";
        List<Funcionario> lista = em.createQuery(jpqlCheck, Funcionario.class)
                                    .setParameter("nome", "João Silva")
                                    .getResultList();
        assertTrue(lista.isEmpty(), "João Silva deveria ter sido removido");
        
        assertNull(em.find(DadosBancarios.class, idDadosBancarios), "Dados bancários deveriam ter sido apagados");
        assertNull(em.find(Tarefa.class, idTarefa), "Tarefas deveriam ter sido apagadas");
        
        logger.info("Funcionário e dependências removidos com sucesso.");
    }
    
    @Test
    public void testBuscarPorCargoJPQL() {
        logger.info("--- Teste JPQL: Busca por Cargo ---");
        List<Funcionario> seniors = buscarFuncionariosPorCargoJPQL("Padeiro Senior");
        assertEquals(1, seniors.size());
        assertEquals("João Silva", seniors.get(0).getNome());
        logger.info("Encontrado 1 Padeiro Senior.");
    }

    @Test
    public void testContarFuncionariosJPQL() {
        logger.info("--- Teste JPQL: Contagem por Padaria ---");
        Long qtd = contarFuncionariosDaPadariaJPQL("Padaria do Melhor Teste");
        assertEquals(3L, qtd);
        logger.info("Total de funcionários na Padaria 1: {}", qtd);
    }
    
    @Test
    public void testEqualsAndHashCode() {
        logger.info("--- Executando testEqualsAndHashCode ---");

        Funcionario f1 = buscarFuncionarioPorIdJPQL(2);
        Funcionario f2 = buscarFuncionarioPorIdJPQL(3);
        Funcionario f3 = buscarFuncionarioPorIdJPQL(2);

        assertFalse(f1.equals(f2), "Os objetos não devem ser iguais");
        assertTrue(f1.equals(f3), "Os objetos devem ser iguais");
        assertEquals(f1.hashCode(), f3.hashCode(), "Hashcodes devem ser iguais");
    }
}