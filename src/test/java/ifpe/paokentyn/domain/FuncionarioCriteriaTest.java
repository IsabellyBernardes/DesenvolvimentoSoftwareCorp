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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FuncionarioCriteriaTest extends GenericTest {
    
    private static final Logger logger = LoggerFactory.getLogger(FuncionarioCriteriaTest.class);

    @Test
    public void testCriteriaBuscaDinamicaNomeSalario() {
        logger.info("--- Busca Dinâmica (Nome & Salário) ---");

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
        logger.info("--- Busca por Cargo Exato ---");

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
        logger.info("--- Busca por Data de Contratação ---");

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
        logger.info("--- Busca com JOIN (Funcionários de uma Padaria) ---");

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
        logger.info("--- COUNT (Total de Funcionários) ---");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Funcionario> root = query.from(Funcionario.class);

        query.select(cb.count(root));

        Long total = em.createQuery(query).getSingleResult();

        assertEquals(3L, total);
        logger.info("Sucesso! Total de funcionários no banco: {}", total);
    }


    @Test
    public void testCriteriaLeftJoinTarefas() {
        logger.info("--- LEFT JOIN (Funcionario -> Tarefas) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Funcionario> root = query.from(Funcionario.class);
        
        Join<Object, Object> joinTarefas = root.join("tarefas", JoinType.LEFT);
        
        query.multiselect(root.get("nome"), joinTarefas.get("descricao"));
        
        List<Tuple> resultado = em.createQuery(query).getResultList();
        
        for (Tuple t : resultado) {
            logger.info("Funcionario: {} | Tarefa: {}", t.get(0), t.get(1));
        }
        assertTrue(resultado.size() >= 3);
    }

    @Test
    public void testCriteriaPathExpression() {
        logger.info("--- Path Expression (Navegação) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Funcionario> query = cb.createQuery(Funcionario.class);
        Root<Funcionario> root = query.from(Funcionario.class);
        
        query.where(cb.equal(root.get("dadosBancarios").get("banco"), "Banco Teste S.A."));
        
        List<Funcionario> lista = em.createQuery(query).getResultList();
        assertEquals(1, lista.size());
        assertEquals("João Silva", lista.get(0).getNome());
        logger.info("Sucesso na navegação por path expression.");
    }

    @Test
    public void testCriteriaMaxMinSalario() {
        logger.info("--- MAX e MIN (Salário) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Funcionario> root = query.from(Funcionario.class);
        
        query.multiselect(
            cb.max(root.get("salario")),
            cb.min(root.get("salario"))
        );
        
        Tuple resultado = em.createQuery(query).getSingleResult();
        
        Double max = resultado.get(0, Double.class);
        Double min = resultado.get(1, Double.class);
        
        logger.info("Maior Salário: {}", max);
        logger.info("Menor Salário: {}", min);
        
        assertNotNull(max);
        assertNotNull(min);
    }

    @Test
    public void testCriteriaDistinctCargos() {
        logger.info("--- DISTINCT (Cargos Únicos) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Funcionario> root = query.from(Funcionario.class);
        
        query.select(root.get("cargo")).distinct(true);
        
        List<String> cargos = em.createQuery(query).getResultList();
        
        logger.info("Cargos encontrados: {}", cargos);
        assertEquals(3, cargos.size());
    }

    @Test
    public void testCriteriaCollectionManipulation() {
        logger.info("--- Manipulação de Coleção (IS NOT EMPTY) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Funcionario> query = cb.createQuery(Funcionario.class);
        Root<Funcionario> root = query.from(Funcionario.class);
        
        query.where(cb.isNotEmpty(root.get("tarefas")));
        
        List<Funcionario> ocupados = em.createQuery(query).getResultList();
        
        assertTrue(ocupados.size() > 0);
        logger.info("Funcionários com tarefas: {}", ocupados.size());
    }

    @Test
    public void testCriteriaNewProjection() {
        logger.info("--- Simulação de NEW (DTO Projection com Tuple) ---");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Funcionario> root = query.from(Funcionario.class);
        
        query.multiselect(
            root.get("nome").alias("nomeFunc"), 
            root.get("cargo").alias("cargoFunc")
        );
        
        List<Tuple> lista = em.createQuery(query).getResultList();
        
        for (Tuple t : lista) {
            logger.info("DTO -> Nome: {}, Cargo: {}", t.get("nomeFunc"), t.get("cargoFunc"));
        }
        assertTrue(lista.size() > 0);
    }
}