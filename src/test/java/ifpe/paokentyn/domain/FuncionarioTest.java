package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuncionarioTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(FuncionarioTest.class);

    private Funcionario buscarFuncionarioPorNome(String nome) {
        String jpql = "SELECT f FROM Funcionario f WHERE f.nome = :nome";
        TypedQuery<Funcionario> query = em.createQuery(jpql, Funcionario.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }

    private Padaria buscarPadariaPorNome(String nome) {
        String jpql = "SELECT p FROM Padaria p WHERE p.nome = :nome";
        TypedQuery<Padaria> query = em.createQuery(jpql, Padaria.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }

    @Test
    public void testEncontrarFuncionarioDoDataSet() {
        logger.info("--- Executando testEncontrarFuncionarioDoDataSet ---");

        Funcionario func = buscarFuncionarioPorNome("João Silva");

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

        // 1. Busca dependência (Padaria) dinamicamente
        Padaria padariaExistente = buscarPadariaPorNome("Padaria do Melhor Teste");
        assertNotNull(padariaExistente);

        // 2. Cria novos objetos
        DadosBancarios novosDados = new DadosBancarios();
        novosDados.setBanco("Banco Novo S.A.");
        novosDados.setAgencia("0002");
        novosDados.setConta("98765-4");
        
        Funcionario novoFunc = new Funcionario();
        novoFunc.setNome("Maria Souza");
        novoFunc.setCargo("Caixa");
        novoFunc.setSalario(2100.00);
        novoFunc.setPadaria(padariaExistente);
        
        novoFunc.setDadosBancarios(novosDados);

        // 3. Persiste
        em.persist(novoFunc);
        em.flush();

        // 4. Valida
        assertNotNull(novoFunc.getId());
        assertTrue(novoFunc.getId() > 0);
        
        // Garante que não é o mesmo ID do João Silva
        Funcionario joao = buscarFuncionarioPorNome("João Silva");
        assertNotEquals(joao.getId(), novoFunc.getId());

        logger.info("Persistido funcionário {} com ID {}", novoFunc.getNome(), novoFunc.getId());
    }
    
    @Test
    public void testAtualizarFuncionarioComMerge() {
        logger.info("--- Executando testAtualizarFuncionarioComMerge ---");
        
        // 1. Busca dinâmica
        Funcionario func = buscarFuncionarioPorNome("João Silva");
        assertNotNull(func);
        Long idOriginal = func.getId();
        
        // 2. Desanexa
        em.clear(); 
        
        // 3. Modifica (Detached)
        func.setCargo("Gerente de Produção");
        func.setSalario(4500.00);
        
        // 4. Merge
        em.merge(func); 
        
        // 5. Sincroniza
        em.flush();
        em.clear();
        
        // 6. Verifica (Buscando pelo ID que descobrimos antes)
        Funcionario funcAtualizado = em.find(Funcionario.class, idOriginal);
        assertEquals("Gerente de Produção", funcAtualizado.getCargo());
        assertEquals(4500.00, funcAtualizado.getSalario());
        
        logger.info("Funcionário atualizado com sucesso.");
    }

    @Test
    public void testRemoverFuncionarioECascade() {
        logger.info("--- Executando testRemoverFuncionarioECascade ---");
        
        // 1. Busca dinâmica
        Funcionario func = buscarFuncionarioPorNome("João Silva");
        assertNotNull(func);
        
        // Captura IDs dos filhos para verificar cascade depois
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
}