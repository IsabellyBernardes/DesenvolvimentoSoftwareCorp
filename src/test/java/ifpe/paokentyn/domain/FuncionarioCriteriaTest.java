/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ifpe.paokentyn.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FuncionarioCriteriaTest extends GenericTest{
    private static final Logger logger = LoggerFactory.getLogger(FuncionarioCriteriaTest.class);

    @Test
    public void testCriteriaBuscaDinamicaNomeSalario() {
        logger.info("--- Teste 1: Criteria - Busca Dinâmica (Nome & Salário) ---");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Funcionario> query = cb.createQuery(Funcionario.class);
        Root<Funcionario> root = query.from(Funcionario.class);
        List<Predicate> condicoes = new ArrayList<>();

        condicoes.add(cb.like(cb.lower(root.get("nome")), "%maria%"));

        condicoes.add(cb.ge(root.get("salario"), 3000.00));

        query.where(cb.and(condicoes.toArray(new Predicate[0])));

        List<Funcionario> result = em.createQuery(query).getResultList();
        
        assertEquals(1, result.size());
        assertEquals("Maria Silva", result.get(0).getNome());
        logger.info("Sucesso! Encontrada: {}", result.get(0).getNome());
    }

    @Test
    public void testCriteriaBuscaPorCargoExato() {
        logger.info("--- Teste 2: Criteria - Busca por Cargo Exato ---");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Funcionario> query = cb.createQuery(Funcionario.class);
        Root<Funcionario> root = query.from(Funcionario.class);

        query.where(cb.equal(root.get("cargo"), "Padeiro Junior"));

        List<Funcionario> juniors = em.createQuery(query).getResultList();

        assertEquals(1, juniors.size());
        assertEquals("Pedro Silva", juniors.get(0).getNome());
        logger.info("Sucesso! Padeiro Junior: {}", juniors.get(0).getNome());
    }

    @Test
    public void testCriteriaBuscaPorDataContratacao() {
        logger.info("--- Teste 3: Criteria - Busca por Data de Contratação ---");

        LocalDate ld = LocalDate.of(2022, 5, 15);
        Date dataAlvo = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Funcionario> query = cb.createQuery(Funcionario.class);
        Root<Funcionario> root = query.from(Funcionario.class);

        query.where(cb.equal(root.get("dataContratacao"), dataAlvo));

        List<Funcionario> contratados = em.createQuery(query).getResultList();

        assertEquals(3, contratados.size());
        logger.info("Sucesso! {} funcionários contratados em {}", contratados.size(), ld);
    }

    @Test
    public void testCriteriaBuscaComJoinPadaria() {
        logger.info("--- Teste 4: Criteria - Busca com JOIN (Funcionários de uma Padaria) ---");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Funcionario> query = cb.createQuery(Funcionario.class);
        Root<Funcionario> root = query.from(Funcionario.class);

        Join<Funcionario, Padaria> joinPadaria = root.join("padaria");

        query.where(cb.equal(joinPadaria.get("nome"), "Padaria do Melhor Teste"));

        List<Funcionario> equipe = em.createQuery(query).getResultList();

        assertEquals(3, equipe.size());
        logger.info("Sucesso! Equipe da Padaria 1 tem {} pessoas.", equipe.size());
    }

    @Test
    public void testCriteriaCountTotalFuncionarios() {
        logger.info("--- Teste 5: Criteria - COUNT (Total de Funcionários) ---");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Funcionario> root = query.from(Funcionario.class);

        query.select(cb.count(root));

        Long total = em.createQuery(query).getSingleResult();

        assertEquals(3L, total);
        logger.info("Sucesso! Total de funcionários no banco: {}", total);
    }
}
