package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioJPQLTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(FuncionarioJPQLTest.class);


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
    public void testBuscarPorCargoJPQL() {
        logger.info("--- Busca por Cargo ---");
        List<Funcionario> seniors = buscarFuncionariosPorCargoJPQL("Padeiro Senior");
        assertEquals(1, seniors.size());
        assertEquals("João Silva", seniors.get(0).getNome());
        logger.info("Encontrado 1 Padeiro Senior.");
    }

    @Test
    public void testContarFuncionariosJPQL() {
        logger.info("--- Contagem por Padaria ---");
        Long qtd = contarFuncionariosDaPadariaJPQL("Padaria do Melhor Teste");
        assertEquals(3L, qtd);
        logger.info("Total de funcionários na Padaria 1: {}", qtd);
    }

    @Test
    public void testJPQLPathExpression() {
        logger.info("--- Path Expression (Navegação com ponto) ---");
        
        String jpql = "SELECT f FROM Funcionario f WHERE f.dadosBancarios.banco = :banco";
        
        List<Funcionario> lista = em.createQuery(jpql, Funcionario.class)
                                    .setParameter("banco", "Banco Teste S.A.")
                                    .getResultList();
                                    
        assertEquals(1, lista.size());
        assertEquals("João Silva", lista.get(0).getNome());
    }

    @Test
    public void testJPQLMaxMinSalario() {
        logger.info("---Funções de Agregação (MAX e MIN) ---");
        
        String jpql = "SELECT MAX(f.salario), MIN(f.salario) FROM Funcionario f";
        
        Object[] resultado = (Object[]) em.createQuery(jpql).getSingleResult();
        
        Double maior = (Double) resultado[0];
        Double menor = (Double) resultado[1];
        
        logger.info("Maior Salário: {}", maior);
        logger.info("Menor Salário: {}", menor);
        
        assertEquals(3400.00, maior);
        assertEquals(3200.00, menor);
    }

    @Test
    public void testJPQLDistinctCargos() {
        logger.info("---DISTINCT (Cargos únicos) ---");
        
        String jpql = "SELECT DISTINCT f.cargo FROM Funcionario f";
        
        List<String> cargos = em.createQuery(jpql, String.class).getResultList();
        
        assertEquals(3, cargos.size());
        logger.info("Cargos encontrados: {}", cargos);
    }

    @Test
    public void testJPQLLeftJoinTarefas() {
        logger.info("--- LEFT JOIN (Funcionários e suas Tarefas) ---");
        
        String jpql = "SELECT f.nome, t.descricao FROM Funcionario f LEFT JOIN f.tarefas t";
        
        List<Object[]> resultados = em.createQuery(jpql).getResultList();
        
        for (Object[] linha : resultados) {
            logger.info("Funcionario: {} | Tarefa: {}", linha[0], linha[1]);
        }
        assertTrue(resultados.size() >= 3);
    }

    @Test
    public void testJPQLCollectionManipulation() {
        logger.info("--- Manipulação de Coleção (IS NOT EMPTY) ---");
        
        String jpql = "SELECT f FROM Funcionario f WHERE f.tarefas IS NOT EMPTY";
        
        List<Funcionario> ocupados = em.createQuery(jpql, Funcionario.class).getResultList();
        
        assertTrue(ocupados.size() > 0);
        logger.info("Funcionários com tarefas: {}", ocupados.size());
    }
    
}