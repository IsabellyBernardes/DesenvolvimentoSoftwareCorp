package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class DadosBancariosTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(DadosBancariosTest.class);

    private DadosBancarios buscarPorContaJPQL(String conta) {
        String jpql = "SELECT d FROM DadosBancarios d WHERE d.conta = :conta";
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("conta", conta);
        return query.getSingleResult();
    }

    private List<DadosBancarios> buscarPorNomeBancoJPQL(String parteNomeBanco) {
        String jpql = "SELECT d FROM DadosBancarios d WHERE LOWER(d.banco) LIKE LOWER(:nome)";
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("nome", "%" + parteNomeBanco + "%");
        return query.getResultList();
    }

    private List<DadosBancarios> buscarPorAgenciaJPQL(String agencia) {
        String jpql = "SELECT d FROM DadosBancarios d WHERE d.agencia = :agencia";
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("agencia", agencia);
        return query.getResultList();
    }

    private DadosBancarios buscarPeloNomeFuncionarioJPQL(String nomeFuncionario) {
        String jpql = "SELECT d FROM DadosBancarios d JOIN d.funcionario f WHERE f.nome = :nome";
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("nome", nomeFuncionario);
        return query.getSingleResult();
    }

    private Long contarContasPorBancoJPQL(String nomeBancoExact) {
        String jpql = "SELECT COUNT(d) FROM DadosBancarios d WHERE d.banco = :banco";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("banco", nomeBancoExact);
        return query.getSingleResult();
    }
    
    private DadosBancarios buscarPorId(long id) {
        String jpql = "SELECT d FROM DadosBancarios d WHERE d.id = :id";
        TypedQuery<DadosBancarios> query = em.createQuery(jpql, DadosBancarios.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Test
    public void testEncontrarDadosBancariosDoDataSet() {
        logger.info("--- Executando testEncontrarDadosBancariosDoDataSet ---");

        DadosBancarios dados = buscarPorContaJPQL("12345-6");

        assertNotNull(dados, "Deveria ter encontrado a conta 12345-6 do dataset");
        assertEquals("Banco Teste S.A.", dados.getBanco());

        assertNotNull(dados.getFuncionario(), "O Funcionario não deveria ser nulo");
        assertEquals("João Silva", dados.getFuncionario().getNome());

        logger.info("Encontrada conta: {} do func. {}",
                dados.getConta(), dados.getFuncionario().getNome());
    }
    
    @Test
    public void testAtualizarDadosGerenciados() {
        logger.info("--- Executando testAtualizarDadosGerenciados ---");

        DadosBancarios dados = buscarPorContaJPQL("12345-6");
        assertNotNull(dados);

        dados.setBanco("Banco Digital Nubank");

        em.flush();
        em.clear();

        DadosBancarios dadosAtualizados = buscarPorContaJPQL("12345-6");
        assertEquals("Banco Digital Nubank", dadosAtualizados.getBanco());

        logger.info("Banco atualizado para: {}", dadosAtualizados.getBanco());
    }

    @Test
    public void testAtualizarDadosComMerge() {
        logger.info("--- Executando testAtualizarDadosComMerge ---");

        DadosBancarios dados = buscarPorContaJPQL("12345-6");
        assertNotNull(dados);
        Long idOriginal = dados.getId(); 

        em.clear();

        dados.setConta("99999-X");
        
        em.merge(dados);

        em.flush();
        em.clear();

        DadosBancarios dadosAtualizados = buscarPorContaJPQL("99999-X");
        assertEquals(idOriginal, dadosAtualizados.getId(), "O ID deve ser o mesmo");
        assertEquals("99999-X", dadosAtualizados.getConta());

        logger.info("Conta atualizada via merge para: {}", dadosAtualizados.getConta());
    }

    @Test
    public void testRemoverDadosBancarios() {
        logger.info("--- Executando testRemoverDadosBancarios ---");

        DadosBancarios dados = buscarPorContaJPQL("12345-6");
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
    public void testBuscarPorNomeBanco() {
        logger.info("--- Executando testBuscarPorNomeBanco ---");

        List<DadosBancarios> lista = buscarPorNomeBancoJPQL("Teste");

        assertEquals(4, lista.size(), "Deveria encontrar 3 bancos com o termo 'Teste'");
        
        logger.info("Sucesso. Encontrados: {} bancos.", lista.size());
    }

    @Test
    public void testBuscarPorAgencia() {
        logger.info("--- Executando testBuscarPorAgencia ---");

        List<DadosBancarios> lista = buscarPorAgenciaJPQL("0002");

        assertEquals(1, lista.size(), "Deveria ter apenas 1 conta nessa agência");
        assertEquals("12345-2", lista.get(0).getConta(), "A conta deveria ser a 12345-2");

        logger.info("Sucesso. Agência 0002 pertence à conta {}", lista.get(0).getConta());
    }

    @Test
    public void testBuscarPeloNomeFuncionario() {
        logger.info("--- Executando testBuscarPeloNomeFuncionario (JOIN) ---");

        DadosBancarios resultado = buscarPeloNomeFuncionarioJPQL("João Silva");

        assertNotNull(resultado, "Deveria achar dados bancários para João Silva");
        assertEquals("12345-6", resultado.getConta());

        logger.info("Sucesso. A conta de João Silva é {}", resultado.getConta());
    }

    @Test
    public void testContarContasPorBanco() {
        logger.info("--- Executando testContarContasPorBanco (COUNT) ---");

        Long quantidade = contarContasPorBancoJPQL("Banco Teste S.A.");

        assertEquals(1L, quantidade, "Deveria existir exatamente 1 conta neste banco");

        logger.info("Sucesso. Contagem correta: {}", quantidade);
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