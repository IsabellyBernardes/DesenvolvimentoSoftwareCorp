package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DadosBancariosTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(DadosBancariosTest.class);

    // --- (JPQL) ---
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
    public void testAtualizarDadosGerenciados() {
        logger.info("--- Executando testAtualizarDadosGerenciados ---");

        // 1. Busca pela conta original
        DadosBancarios dados = buscarPorConta("12345-6");
        assertNotNull(dados);

        // 2. Altera o objeto gerenciado (Managed)
        dados.setBanco("Banco Digital Nubank");

        // 3. Sincroniza
        em.flush();
        em.clear();

        // 4. Verifica se atualizou
        DadosBancarios dadosAtualizados = buscarPorConta("12345-6");
        assertEquals("Banco Digital Nubank", dadosAtualizados.getBanco());

        logger.info("Banco atualizado para: {}", dadosAtualizados.getBanco());
    }

    @Test
    public void testAtualizarDadosComMerge() {
        logger.info("--- Executando testAtualizarDadosComMerge ---");

        // 1. Busca pela conta original
        DadosBancarios dados = buscarPorConta("12345-6");
        assertNotNull(dados);
        Long idOriginal = dados.getId(); // Guarda o ID gerado pelo banco para conferir depois

        // 2. Desanexa (Detached)
        em.clear();

        // 3. Modifica (Detached) - Mudando a própria chave de busca (Conta)
        dados.setConta("99999-X");

        // 4. Merge
        em.merge(dados);

        // 5. Sincroniza
        em.flush();
        em.clear();

        // 6. Verifica buscando pela NOVA conta
        DadosBancarios dadosAtualizados = buscarPorConta("99999-X");
        assertEquals(idOriginal, dadosAtualizados.getId(), "O ID deve ser o mesmo");
        assertEquals("99999-X", dadosAtualizados.getConta());

        logger.info("Conta atualizada via merge para: {}", dadosAtualizados.getConta());
    }

    @Test
    public void testRemoverDadosBancarios() {
        logger.info("--- Executando testRemoverDadosBancarios ---");

        // 1. Busca dinâmica
        DadosBancarios dados = buscarPorConta("12345-6");
        assertNotNull(dados);

        // 2. PREPARAÇÃO: Desvincular do Funcionário (Dono)
        // Importante: O Funcionário tem a FK. Se deletarmos os dados sem limpar a FK dele, o banco reclama.
        Funcionario dono = dados.getFuncionario();
        dono.setDadosBancarios(null);

        // Força o update do funcionário (FK = null) ANTES de apagar a conta
        em.flush();

        // 3. Remove a conta
        em.remove(dados);

        // 4. Sincroniza
        em.flush();
        em.clear();

        // 5. Verifica se sumiu (A query deve retornar vazio)
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
