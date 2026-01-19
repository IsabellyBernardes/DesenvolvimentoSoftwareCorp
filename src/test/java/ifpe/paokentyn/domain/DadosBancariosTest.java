package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class DadosBancariosTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(DadosBancariosTest.class);

    private DadosBancarios buscarPorConta(String conta) {
        String jpql = "SELECT d FROM DadosBancarios d WHERE d.conta = :conta";
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("conta", conta);
        return query.getSingleResult();
    }

    private DadosBancarios buscarPorId(int id) {
        String jpql = "SELECT d FROM DadosBancarios d WHERE d.id = :id";
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }
    
    private List<DadosBancarios> buscarDadosDinamico(String banco, String agencia) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DadosBancarios> query = cb.createQuery(DadosBancarios.class);
        Root<DadosBancarios> root = query.from(DadosBancarios.class);
        List<Predicate> condicoes = new ArrayList<>();

        if (banco != null) {
            condicoes.add(cb.like(root.get("banco"), "%" + banco + "%"));
        }
        if (agencia != null) {
            condicoes.add(cb.equal(root.get("agencia"), agencia));
        }

        query.where(cb.and(condicoes.toArray(new Predicate[0])));
        return em.createQuery(query).getResultList();
    }

    @Test
    public void testEncontrarDadosBancariosDoDataSet() {
        logger.info("--- Executando testEncontrarDadosBancariosDoDataSet ---");

        DadosBancarios dados = buscarPorConta("12345-6");

        assertNotNull(dados, "Deveria ter encontrado a conta 12345-6 do dataset");
        assertEquals("Banco Teste S.A.", dados.getBanco());

        assertNotNull(dados.getFuncionario(), "O Funcionario não deveria ser nulo");
        assertEquals("João Silva", dados.getFuncionario().getNome());

        logger.info("Encontrada conta: {} do func. {}",
                dados.getConta(), dados.getFuncionario().getNome());
    }
    
    @Test
    public void testBuscaDinamicaComCriteria() {
        logger.info("--- Executando testBuscaDinamicaComCriteria (DadosBancarios) ---");

        // Cenário A: Busca por Banco ("Teste") -> Todos os 3
        List<DadosBancarios> todos = buscarDadosDinamico("Teste", null);
        assertEquals(3, todos.size());

        // Cenário B: Busca por Agência Específica ("0002")
        List<DadosBancarios> agencia2 = buscarDadosDinamico(null, "0002");
        assertEquals(1, agencia2.size());
        assertEquals("12345-2", agencia2.get(0).getConta());

        // Cenário C: Combinação Banco + Agência
        List<DadosBancarios> combo = buscarDadosDinamico("Teste", "0003");
        assertEquals(1, combo.size());
        assertEquals("12345-3", combo.get(0).getConta());

        logger.info("Teste Criteria DadosBancarios OK.");
    }

    @Test
    public void testAtualizarDadosGerenciados() {
        logger.info("--- Executando testAtualizarDadosGerenciados ---");

        DadosBancarios dados = buscarPorConta("12345-6");
        assertNotNull(dados);

        dados.setBanco("Banco Digital Nubank");

        em.flush();
        em.clear();

        DadosBancarios dadosAtualizados = buscarPorConta("12345-6");
        assertEquals("Banco Digital Nubank", dadosAtualizados.getBanco());

        logger.info("Banco atualizado para: {}", dadosAtualizados.getBanco());
    }

    @Test
    public void testAtualizarDadosComMerge() {
        logger.info("--- Executando testAtualizarDadosComMerge ---");

        DadosBancarios dados = buscarPorConta("12345-6");
        assertNotNull(dados);
        Long idOriginal = dados.getId(); 

        em.clear();

        dados.setConta("99999-X");
        
        em.merge(dados);

        em.flush();
        em.clear();

        DadosBancarios dadosAtualizados = buscarPorConta("99999-X");
        assertEquals(idOriginal, dadosAtualizados.getId(), "O ID deve ser o mesmo");
        assertEquals("99999-X", dadosAtualizados.getConta());

        logger.info("Conta atualizada via merge para: {}", dadosAtualizados.getConta());
    }

    @Test
    public void testRemoverDadosBancarios() {
        logger.info("--- Executando testRemoverDadosBancarios ---");

        DadosBancarios dados = buscarPorConta("12345-6");
        assertNotNull(dados);

        Funcionario dono = dados.getFuncionario();
        dono.setDadosBancarios(null);

        em.flush();

        em.remove(dados);

        em.flush();
        em.clear();

        String jpqlCheck = "SELECT d FROM DadosBancarios d WHERE d.conta = :conta";
        var lista = em.createQuery(jpqlCheck, DadosBancarios.class)
                .setParameter("conta", "12345-6")
                .getResultList();

        assertTrue(lista.isEmpty(), "A conta 12345-6 deveria ter sido removida");

        logger.info("Dados Bancários removidos com sucesso.");
    }

    @Test
    public void testEqualsAndHashCode() {
        logger.info("--- Executando testEqualsAndHashCode ---");

        DadosBancarios db1 = buscarPorId(2);
        DadosBancarios db2 = buscarPorId(3);
        DadosBancarios db3 = buscarPorId(2);

        assertFalse(db1.equals(db2), "Os objetos não devem ser iguais");
        assertTrue(db1.equals(db3), "Os objetos devem ser iguais");
        assertEquals(db1.hashCode(), db3.hashCode(), "Hashcodes devem ser iguais");
    }
}