/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ifpe.paokentyn.domain;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DadosBancariosCriteriaTest extends GenericTest{
    private static final Logger logger = LoggerFactory.getLogger(DadosBancariosCriteriaTest.class);

    @Test
    public void testCriteriaBuscaExataPorConta() {
        logger.info("--- Teste 1: Criteria - Busca Exata por Conta ---");
        
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
        logger.info("--- Teste 2: Criteria - Busca Parcial (LIKE) por Banco ---");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DadosBancarios> query = cb.createQuery(DadosBancarios.class);
        Root<DadosBancarios> root = query.from(DadosBancarios.class);

        query.where(cb.like(cb.lower(root.get("banco")), "%teste%"));

        List<DadosBancarios> resultados = em.createQuery(query).getResultList();

        assertEquals(3, resultados.size());
        logger.info("Sucesso! Encontrados {} bancos com a palavra 'teste'.", resultados.size());
    }

    @Test
    public void testCriteriaBuscaPorAgencia() {
        logger.info("--- Teste 3: Criteria - Busca por Agência ---");

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
        logger.info("--- Teste 4: Criteria - Busca com JOIN (Pelo nome do Dono) ---");

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
        logger.info("--- Teste 5: Criteria - Agregação (COUNT) ---");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<DadosBancarios> root = query.from(DadosBancarios.class);

        query.select(cb.count(root));
        
        query.where(cb.equal(root.get("banco"), "Banco Teste S.A. 2"));

        Long quantidade = em.createQuery(query).getSingleResult();

        assertEquals(1L, quantidade);
        logger.info("Sucesso! Existem {} contas neste banco.", quantidade);
    }
}
