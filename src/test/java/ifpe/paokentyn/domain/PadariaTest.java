package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PadariaTest extends GenericTest {

    private Padaria buscarPadariaPorNome(String nome) {
        String jpql = "SELECT p FROM Padaria p WHERE p.nome = :nome";
        TypedQuery<Padaria> query = em.createQuery(jpql, Padaria.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }
    
    private Padaria buscarPadariaPorId(int id) {
        String jpql = "SELECT p FROM Padaria p WHERE p.id = :id";
        TypedQuery<Padaria> query = em.createQuery(jpql, Padaria.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }
    
    @Test
    public void testPersistirPadaria() {
        logger.info("--- Executando testPersistirPadaria ---");
        Padaria novaPadaria = new Padaria();
        novaPadaria.setNome("Padaria Central");
        
        novaPadaria.setCnpj("09803905000174");
        novaPadaria.setCep("54730-000"); 

        em.persist(novaPadaria);
        em.flush();
        assertNotNull(novaPadaria.getId());
    }

    @Test
    public void testEncontrarPadariaDoDataSet() {
        logger.info("--- Executando testEncontrarPadariaDoDataSet ---");
        
        Padaria padaria = buscarPadariaPorNome("Padaria do Melhor Teste");

        assertNotNull(padaria);
        assertEquals("Padaria do Melhor Teste", padaria.getNome());
        assertEquals("99887766000199", padaria.getCnpj());
        
        assertEquals(1L, padaria.getId(), "O banco deveria ter gerado ID 1 para a primeira padaria");

        logger.info("Padaria encontrada: ID={}, nome={}", padaria.getId(), padaria.getNome());
    }
    
    @Test
    public void testAtualizarPadariaGerenciada() {
        logger.info("--- Executando testAtualizarPadariaGerenciada (Sem Merge) ---");
        Padaria padaria = em.find(Padaria.class, 1L);
        padaria.setNome("Padaria Central Atualizada");
        
        padaria.setCnpj("09803905000174"); 
        padaria.setCep("50000-000");
        
        em.flush();
        assertEquals("Padaria Central Atualizada", em.find(Padaria.class, 1L).getNome());
    }

    @Test
    public void testAtualizarPadariaComMerge() {
        logger.info("--- Executando testAtualizarPadariaComMerge ---");
        Padaria padaria = em.find(Padaria.class, 2L);
        em.clear();
        
        padaria.setNome("Padaria Secundária Atualizada");
        
        padaria.setCnpj("09803905000174"); 
        padaria.setCep("55555-555");
        
        em.merge(padaria);
        em.flush();
        assertEquals("Padaria Secundária Atualizada", em.find(Padaria.class, 2L).getNome());
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
    
    @Test
    public void testEqualsAndHashCode() {
        logger.info("--- Executando testEqualsAndHashCode ---");

        Padaria p1 = buscarPadariaPorId(2);
        Padaria p2 = buscarPadariaPorId(3);
        Padaria p3 = buscarPadariaPorId(2);

        assertFalse(p1.equals(p2), "Os objetos não devem ser iguais");
        assertTrue(p1.equals(p3), "Os objetos devem ser iguais");
        assertEquals(p1.hashCode(), p3.hashCode(), "Hashcodes devem ser iguais");
    }
}