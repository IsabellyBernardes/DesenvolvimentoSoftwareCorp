package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PadariaTest extends GenericTest {

    private final static String CNPJ_NOVO_TESTE = "11223344000199"; 
    private final static String CNPJ_DATASET = "99887766000199"; 


    private Padaria buscarPadariaPorNome(String nome) {
        String jpql = "SELECT p FROM Padaria p WHERE p.nome = :nome";
        TypedQuery<Padaria> query = em.createQuery(jpql, Padaria.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }

    @Test
    public void testPersistirPadaria() {
        logger.info("--- Executando testPersistirPadaria ---");
        
        Padaria novaPadaria = new Padaria();
        novaPadaria.setNome("Pão Kentyn - Filial Agreste");
        novaPadaria.setCep("55000000");
        novaPadaria.setCnpj(CNPJ_NOVO_TESTE);

        logger.info("Persistindo nova padaria...");

        em.persist(novaPadaria);
        em.flush();

        assertNotNull(novaPadaria.getId());
        assertTrue(novaPadaria.getId() > 0);
        assertEquals(CNPJ_NOVO_TESTE, novaPadaria.getCnpj());
        
        Padaria padariaDataset = buscarPadariaPorNome("Padaria do Melhor Teste");
        assertNotEquals(padariaDataset.getId(), novaPadaria.getId());

        logger.info("Padaria persistida com sucesso! ID gerado={}", novaPadaria.getId());
    }

    @Test
    public void testEncontrarPadariaDoDataSet() {
        logger.info("--- Executando testEncontrarPadariaDoDataSet ---");
        
        Padaria padaria = buscarPadariaPorNome("Padaria do Melhor Teste");

        assertNotNull(padaria);
        assertEquals("Padaria do Melhor Teste", padaria.getNome());
        assertEquals(CNPJ_DATASET, padaria.getCnpj());
        
        assertEquals(1L, padaria.getId(), "O banco deveria ter gerado ID 1 para a primeira padaria");

        logger.info("Padaria encontrada: ID={}, nome={}", padaria.getId(), padaria.getNome());
    }

    @Test
    public void testAtualizarPadariaComMerge() {
        logger.info("--- Executando testAtualizarPadariaComMerge ---");

        Padaria padaria = buscarPadariaPorNome("Padaria do Melhor Teste");
        assertNotNull(padaria);
        Long idOriginal = padaria.getId();

        em.clear();

        padaria.setNome("Padaria Renomeada S.A.");

        em.merge(padaria);

        em.flush();
        em.clear();

        Padaria padariaAtualizada = em.find(Padaria.class, idOriginal);
        assertEquals("Padaria Renomeada S.A.", padariaAtualizada.getNome());

        logger.info("Padaria renomeada com sucesso.");
    }

    @Test
    public void testRemoverPadariaECascade() {
        logger.info("--- Executando testRemoverPadariaECascade ---");

        Padaria padaria = buscarPadariaPorNome("Padaria do Melhor Teste");
        assertNotNull(padaria);

        assertFalse(padaria.getFuncionarios().isEmpty());
        Long idFuncionario = padaria.getFuncionarios().get(0).getId();
        
        assertFalse(padaria.getFornadas().isEmpty());
        Long idFornada = padaria.getFornadas().get(0).getId();

        logger.info("Removendo Padaria ID={}", padaria.getId());

        em.remove(padaria);

        em.flush();
        em.clear();

        String jpqlCheck = "SELECT p FROM Padaria p WHERE p.nome = :nome";
        List<Padaria> lista = em.createQuery(jpqlCheck, Padaria.class)
                                .setParameter("nome", "Padaria do Melhor Teste")
                                .getResultList();
        assertTrue(lista.isEmpty(), "A padaria deveria ter sido removida");

        assertNull(em.find(Funcionario.class, idFuncionario), "Funcionario removido em cascata");
        assertNull(em.find(Fornada.class, idFornada), "Fornada removida em cascata");

        logger.info("Padaria e dependentes removidos com sucesso.");
    }
}