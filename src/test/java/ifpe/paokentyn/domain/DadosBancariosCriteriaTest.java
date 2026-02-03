/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ifpe.paokentyn.domain;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DadosBancariosCriteriaTest extends GenericTest {
    
    private static final Logger logger = LoggerFactory.getLogger(DadosBancariosCriteriaTest.class);
    
    @Test
    public void testCriteriaBuscaExataPorConta() {
        logger.info("--- Busca Exata por Conta ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DadosBancarios> query = cb.createQuery(DadosBancarios.class);
        Root<DadosBancarios> root = query.from(DadosBancarios.class);

        query.where(cb.equal(root.get("conta"), "12345-6"));

        DadosBancarios resultado = em.createQuery(query).getSingleResult();

        assertNotNull(resultado);
        assertEquals("Banco Teste S.A.", resultado.getBanco());
        logger.info("Sucesso! Conta encontrada: {}", resultado.getConta());
    }

    @Test
    public void testCriteriaBuscaParcialPorNomeBanco() {
        logger.info("--- Busca Parcial (LIKE) por Banco ---");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DadosBancarios> query = cb.createQuery(DadosBancarios.class);
        Root<DadosBancarios> root = query.from(DadosBancarios.class);

        query.where(cb.like(cb.lower(root.get("banco")), "%teste%"));

        List<DadosBancarios> resultados = em.createQuery(query).getResultList();

        assertTrue(resultados.size() >= 3); 
        logger.info("Sucesso! Encontrados {} bancos com a palavra 'teste'.", resultados.size());
    }

    @Test
    public void testCriteriaBuscaPorAgencia() {
        logger.info("--- Busca por Agência ---");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DadosBancarios> query = cb.createQuery(DadosBancarios.class);
        Root<DadosBancarios> root = query.from(DadosBancarios.class);

        query.where(cb.equal(root.get("agencia"), "0002"));

        List<DadosBancarios> resultados = em.createQuery(query).getResultList();

        assertEquals(1, resultados.size());
        assertEquals("12345-2", resultados.get(0).getConta());
        logger.info("Sucesso! Agência 0002 pertence à conta {}", resultados.get(0).getConta());
    }

    @Test
    public void testCriteriaBuscaComJoinPeloFuncionario() {
        logger.info("--- Busca com JOIN (Pelo nome do Dono) ---");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DadosBancarios> query = cb.createQuery(DadosBancarios.class);
        Root<DadosBancarios> root = query.from(DadosBancarios.class);

        Join<DadosBancarios, Funcionario> joinFuncionario = root.join("funcionario");
        
        query.where(cb.equal(joinFuncionario.get("nome"), "João Silva"));

        DadosBancarios resultado = em.createQuery(query).getSingleResult();

        assertEquals("12345-6", resultado.getConta());
        logger.info("Sucesso! A conta do João Silva é {}", resultado.getConta());
    }

    @Test
    public void testCriteriaContagemDeContas() {
        logger.info("--- Agregação (COUNT) ---");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<DadosBancarios> root = query.from(DadosBancarios.class);

        query.select(cb.count(root));
        
        query.where(cb.equal(root.get("banco"), "Banco Teste S.A. 2"));

        Long quantidade = em.createQuery(query).getSingleResult();

        assertTrue(quantidade >= 1);
        logger.info("Sucesso! Existem {} contas neste banco.", quantidade);
    }

    @Test
    public void testCriteriaLeftJoin() {
        logger.info("--- LEFT JOIN (Criteria) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<DadosBancarios> root = query.from(DadosBancarios.class);
        
        Join<DadosBancarios, Funcionario> joinFunc = root.join("funcionario", JoinType.LEFT);
        
        query.multiselect(root.get("conta"), joinFunc.get("nome"));
        
        List<Tuple> resultado = em.createQuery(query).getResultList();
        
        for (Tuple t : resultado) {
            logger.info("Conta: {} | Dono: {}", t.get(0), t.get(1));
        }
        assertTrue(resultado.size() > 0);
    }

    @Test
    public void testCriteriaPathExpression() {
        logger.info("--- Path Expression (Navegação) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DadosBancarios> query = cb.createQuery(DadosBancarios.class);
        Root<DadosBancarios> root = query.from(DadosBancarios.class);
        
        query.where(cb.equal(root.get("funcionario").get("nome"), "João Silva"));
        
        DadosBancarios db = em.createQuery(query).getSingleResult();
        assertEquals("12345-6", db.getConta());
    }

    @Test
    public void testCriteriaMaxMin() {
        logger.info("--- MAX e MIN (Criteria) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<DadosBancarios> root = query.from(DadosBancarios.class);
        
        query.multiselect(
            cb.min(root.get("id")), 
            cb.max(root.get("id"))
        );
        
        Tuple resultado = em.createQuery(query).getSingleResult();
        
        logger.info("Min ID: {}", resultado.get(0));
        logger.info("Max ID: {}", resultado.get(1));
        assertNotNull(resultado.get(0));
    }

    @Test
    public void testCriteriaDistinct() {
        logger.info("--- DISTINCT (Criteria) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<DadosBancarios> root = query.from(DadosBancarios.class);
        
        query.select(root.get("banco")).distinct(true); 
        
        List<String> bancos = em.createQuery(query).getResultList();
        
        logger.info("Bancos únicos encontrados: {}", bancos);
        assertTrue(bancos.size() >= 3);
    }

    @Test
    public void testCriteriaCollectionManipulation() {
        logger.info("--- Manipulação de Coleção (SIZE/IS NOT EMPTY) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DadosBancarios> query = cb.createQuery(DadosBancarios.class);
        Root<DadosBancarios> root = query.from(DadosBancarios.class);
        
        query.where(cb.isNotEmpty(root.get("funcionario").get("tarefas")));
        
        List<DadosBancarios> lista = em.createQuery(query).getResultList();
        
        logger.info("Contas de funcionários ocupados: {}", lista.size());
        assertTrue(!lista.isEmpty());
    }

    @Test
    public void testCriteriaNewProjection() {
        logger.info("--- Simulação de NEW (DTO Projection) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<DadosBancarios> root = query.from(DadosBancarios.class);
        
        query.multiselect(root.get("banco").alias("banco"), root.get("conta").alias("conta"));
        
        List<Tuple> lista = em.createQuery(query).getResultList();
        
        for (Tuple t : lista) {
            String b = t.get("banco", String.class);
            String c = t.get("conta", String.class);
            logger.info("DTO Simulado -> Banco: {}, Conta: {}", b, c);
        }
        assertTrue(lista.size() > 0);
    }
}
