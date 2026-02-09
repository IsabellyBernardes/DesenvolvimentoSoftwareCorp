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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FornadaCriteriaTest extends GenericTest {
    
    private static final Logger logger = LoggerFactory.getLogger(FornadaCriteriaTest.class);

    @Test
    public void testCriteriaBuscaSimplesPorId() {
        logger.info("--- Busca por ID ---");
        
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
        logger.info("--- JOIN com Padaria (Busca exata) ---");
        
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
        logger.info("--- JOIN com LIKE (Dinâmico) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fornada> query = cb.createQuery(Fornada.class);
        Root<Fornada> root = query.from(Fornada.class);
        
        Join<Fornada, Padaria> joinPadaria = root.join("padaria");
        
        query.where(cb.like(joinPadaria.get("nome"), "%Teste DOIS%"));
        
        List<Fornada> lista = em.createQuery(query).getResultList();
        
        assertEquals(2, lista.size(), "Padaria 2 tem 2 fornadas");
        logger.info("Sucesso no LIKE.");
    }

    @Test
    public void testCriteriaBuscaPorData() {
        logger.info("--- Busca por Data ---");
        
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
        logger.info("--- COUNT (Agregação) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Fornada> root = query.from(Fornada.class);
        
        Join<Fornada, Padaria> joinPadaria = root.join("padaria");

        query.select(cb.count(root));
        
        query.where(cb.equal(joinPadaria.get("nome"), "Padaria do Melhor Teste DOIS"));
        
        Long qtd = em.createQuery(query).getSingleResult();
        
        assertEquals(2L, qtd);
        logger.info("Contagem correta: {} fornadas.", qtd);
    }


    @Test
    public void testCriteriaLeftJoin() {
        logger.info("--- LEFT JOIN (Fornada -> ItensPedidos) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Fornada> root = query.from(Fornada.class);
        
        Join<Fornada, ItemPedido> joinItem = root.join("itensPedidos", JoinType.LEFT);
        
        query.multiselect(root.get("id"), joinItem.get("id"));
        
        List<Tuple> resultado = em.createQuery(query).getResultList();
        
        assertTrue(resultado.size() >= 3);
        logger.info("Linhas retornadas no Left Join: {}", resultado.size());
    }

    @Test
    public void testCriteriaPathExpression() {
        logger.info("--- Path Expression (Navegação) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fornada> query = cb.createQuery(Fornada.class);
        Root<Fornada> root = query.from(Fornada.class);
        
        query.where(cb.equal(root.get("padaria").get("cnpj"), "99887766000199"));
        
        List<Fornada> lista = em.createQuery(query).getResultList();
        assertEquals(1, lista.size());
        logger.info("Sucesso! Navegamos até o CNPJ da Padaria 1.");
    }

    @Test
    public void testCriteriaMaxMin() {
        logger.info("--- MAX e MIN (Criteria) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Fornada> root = query.from(Fornada.class);
        
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
        logger.info("--- DISTINCT (Padarias Únicas) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Padaria> query = cb.createQuery(Padaria.class);
        Root<Fornada> root = query.from(Fornada.class);
        
        query.select(root.get("padaria")).distinct(true);
        
        List<Padaria> padarias = em.createQuery(query).getResultList();
        
        assertEquals(2, padarias.size());
        logger.info("Padarias ativas encontradas: {}", padarias.size());
    }

    @Test
    public void testCriteriaCollectionManipulation() {
        logger.info("--- Manipulação de Coleção (SIZE/IS NOT EMPTY) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fornada> query = cb.createQuery(Fornada.class);
        Root<Fornada> root = query.from(Fornada.class);
        
        query.where(cb.isNotEmpty(root.get("itensPedidos")));
        
        List<Fornada> lista = em.createQuery(query).getResultList();
        
        assertTrue(lista.size() > 0);
        logger.info("Fornadas com itens vendidos: {}", lista.size());
    }

    @Test
    public void testCriteriaNewProjection() {
        logger.info("--- Simulação de NEW (DTO Projection) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Fornada> root = query.from(Fornada.class);
        
        query.multiselect(
            root.get("id").alias("idFornada"), 
            root.get("dataFornada").alias("data")
        );
        
        List<Tuple> lista = em.createQuery(query).getResultList();
        
        for (Tuple t : lista) {
            logger.info("DTO -> ID: {}, Data: {}", t.get("idFornada"), t.get("data"));
        }
        assertTrue(lista.size() > 0);
    }
}