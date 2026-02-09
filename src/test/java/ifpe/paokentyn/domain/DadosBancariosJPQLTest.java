package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DadosBancariosJPQLTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(DadosBancariosJPQLTest.class);


    @Test
    public void testJPQLBuscaExataPorConta() {
        logger.info("--- Busca Exata por Conta ---");

        String jpql = "SELECT d FROM DadosBancarios d WHERE d.conta = :conta";
        
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("conta", "12345-6");

        DadosBancarios resultado = query.getSingleResult();

        assertNotNull(resultado);
        assertEquals("Banco Teste S.A.", resultado.getBanco());
        logger.info("Sucesso! Conta encontrada: {}", resultado.getConta());
    }

    @Test
    public void testJPQLBuscaParcialPorNomeBanco() {
        logger.info("--- Busca (LIKE) por Banco ---");

        String jpql = "SELECT d FROM DadosBancarios d WHERE LOWER(d.banco) LIKE :banco";
        
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("banco", "%teste%");

        List<DadosBancarios> resultados = query.getResultList();

        assertEquals(4, resultados.size());
        logger.info("Sucesso! Encontrados {} bancos com a palavra 'teste'.", resultados.size());
    }

    @Test
    public void testJPQLBuscaPorAgencia() {
        logger.info("--- Busca por Agência ---");

        String jpql = "SELECT d FROM DadosBancarios d WHERE d.agencia = :agencia";
        
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("agencia", "0002");

        List<DadosBancarios> resultados = query.getResultList();

        assertEquals(1, resultados.size());
        assertEquals("12345-2", resultados.get(0).getConta());
        logger.info("Sucesso! Agência 0002 pertence à conta {}", resultados.get(0).getConta());
    }

    @Test
    public void testJPQLBuscaComJoinPeloFuncionario() {
        logger.info("--- Busca com JOIN (Pelo nome do Dono) ---");

        String jpql = "SELECT d FROM DadosBancarios d JOIN d.funcionario f WHERE f.nome = :nome";
        
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("nome", "João Silva");

        DadosBancarios resultado = query.getSingleResult();

        assertEquals("12345-6", resultado.getConta());
        logger.info("Sucesso! A conta do João Silva é {}", resultado.getConta());
    }

    @Test
    public void testJPQLContagemDeContas() {
        logger.info("--- Agregação (COUNT) ---");

        String jpql = "SELECT COUNT(d) FROM DadosBancarios d WHERE d.banco = :banco";
        
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("banco", "Banco Teste S.A. DOIS");

        Long quantidade = query.getSingleResult();

        assertEquals(2L, quantidade);
        logger.info("Sucesso! Existem {} contas neste banco.", quantidade);
    }

    @Test
    public void testJPQLPathExpression() {
        logger.info("--- Path Expression (Navegação com ponto) ---");
        
        String jpql = "SELECT d FROM DadosBancarios d WHERE d.funcionario.nome = :nome";
        
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("nome", "João Silva");
        
        DadosBancarios resultado = query.getSingleResult();
        assertEquals("12345-6", resultado.getConta());
    }

    @Test
    public void testJPQLMaxMinIds() {
        logger.info("--- Funções MAX e MIN ---");
        
        String jpql = "SELECT MIN(d.id), MAX(d.id) FROM DadosBancarios d";
        
        Object[] resultado = (Object[]) em.createQuery(jpql).getSingleResult();
        
        Long minId = (Long) resultado[0];
        Long maxId = (Long) resultado[1];
        
        logger.info("Menor ID: {}", minId);
        logger.info("Maior ID: {}", maxId);
        
        assertNotNull(minId);
        assertNotNull(maxId);
    }

    @Test
    public void testJPQLDistinctBancos() {
        logger.info("--- DISTINCT (Nomes de Bancos únicos) ---");
        
        String jpql = "SELECT DISTINCT d.banco FROM DadosBancarios d";
        
        List<String> bancos = em.createQuery(jpql, String.class).getResultList();
        
        assertEquals(3, bancos.size()); 
        logger.info("Bancos distintos encontrados: {}", bancos);
    }

    @Test
    public void testJPQLLeftJoin() {
        logger.info("---  LEFT JOIN (Dados Bancários e Funcionários) ---");
        
        String jpql = "SELECT d.conta, f.nome FROM DadosBancarios d LEFT JOIN d.funcionario f";
        
        List<Object[]> resultados = em.createQuery(jpql).getResultList();
        
        assertEquals(4, resultados.size());
        for (Object[] linha : resultados) {
            logger.info("Conta: {} | Dono: {}", linha[0], linha[1]);
        }
    }

    @Test
    public void testJPQLCollectionManipulation() {
        logger.info("--- Manipulação de Coleção (IS NOT EMPTY) ---");
        
        String jpql = "SELECT d FROM DadosBancarios d WHERE d.funcionario.tarefas IS NOT EMPTY";
        
        List<DadosBancarios> lista = em.createQuery(jpql, DadosBancarios.class).getResultList();
        
        assertTrue(lista.size() > 0);
        logger.info("Contas de funcionários com tarefas pendentes: {}", lista.size());
    }
}