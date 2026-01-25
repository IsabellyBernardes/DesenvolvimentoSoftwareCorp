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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FornadaCriteriaTest extends GenericTest{
    private static final Logger logger = LoggerFactory.getLogger(FornadaCriteriaTest.class);

    @Test
    public void testCriteriaBuscaSimplesPorId() {
        logger.info("--- Teste 1: Criteria - Busca por ID ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fornada> query = cb.createQuery(Fornada.class);
        Root<Fornada> root = query.from(Fornada.class);
        
        query.where(cb.equal(root.get("id"), 1L));
        
        Fornada f = em.createQuery(query).getSingleResult();
        
        assertNotNull(f);
        assertEquals(1L, f.getId());
        logger.info("Fornada 1 encontrada: Padaria {}", f.getPadaria().getNome());
    }

    @Test
    public void testCriteriaBuscaComJoinPadaria() {
        logger.info("--- Teste 2: Criteria - JOIN com Padaria (Busca exata) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fornada> query = cb.createQuery(Fornada.class);
        Root<Fornada> root = query.from(Fornada.class);
        
        Join<Fornada, Padaria> joinPadaria = root.join("padaria");
        
        query.where(cb.equal(joinPadaria.get("nome"), "Padaria do Melhor Teste"));
        
        List<Fornada> lista = em.createQuery(query).getResultList();
        
        assertEquals(1, lista.size(), "Deveria ter apenas 1 fornada nesta padaria");
        logger.info("Sucesso no JOIN.");
    }

    @Test
    public void testCriteriaBuscaPadariaLike() {
        logger.info("--- Teste 3: Criteria - JOIN com LIKE (Dinâmico) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fornada> query = cb.createQuery(Fornada.class);
        Root<Fornada> root = query.from(Fornada.class);
        
        Join<Fornada, Padaria> joinPadaria = root.join("padaria");
        
        query.where(cb.like(joinPadaria.get("nome"), "%Teste 2%"));
        
        List<Fornada> lista = em.createQuery(query).getResultList();
        
        assertEquals(2, lista.size(), "Padaria 2 tem 2 fornadas");
        logger.info("Sucesso no LIKE.");
    }

    @Test
    public void testCriteriaBuscaPorData() {
        logger.info("--- Teste 4: Criteria - Busca por Data ---");
        
        LocalDate localDate = LocalDate.of(2025, 11, 9);
        Date dataAlvo = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fornada> query = cb.createQuery(Fornada.class);
        Root<Fornada> root = query.from(Fornada.class);
        
        query.where(cb.equal(root.get("dataFornada"), dataAlvo));
        
        List<Fornada> lista = em.createQuery(query).getResultList();
        
        assertEquals(3, lista.size(), "Todas as 3 fornadas são desta data");
        logger.info("Sucesso na busca por Data.");
    }

    @Test
    public void testCriteriaCountFornadas() {
        logger.info("--- Teste 5: Criteria - COUNT (Agregação) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Fornada> root = query.from(Fornada.class);
        
        Join<Fornada, Padaria> joinPadaria = root.join("padaria");

        query.select(cb.count(root));
        
        query.where(cb.equal(joinPadaria.get("nome"), "Padaria do Melhor Teste 2"));
        
        Long qtd = em.createQuery(query).getSingleResult();
        
        assertEquals(2L, qtd);
        logger.info("Contagem correta: {} fornadas.", qtd);
    }
}
