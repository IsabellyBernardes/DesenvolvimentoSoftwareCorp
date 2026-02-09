package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import java.util.List;

public class FornadaTest extends GenericTest {

    private Padaria buscarPadariaPorNomeJPQL(String nome) {
        String jpql = "SELECT p FROM Padaria p WHERE p.nome = :nome";
        TypedQuery<Padaria> query = em.createQuery(jpql, Padaria.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }
    
    private Fornada buscarFornadaDaPadariaJPQL(String nomePadaria) {
        String jpql = "SELECT f FROM Fornada f WHERE f.padaria.nome = :nomePadaria";
        TypedQuery<Fornada> query = em.createQuery(jpql, Fornada.class);
        query.setParameter("nomePadaria", nomePadaria);
        return query.getResultList().stream().findFirst().orElse(null);
    }
    
    private Fornada buscarFornadaPorIdJPQL(long id) {
        String jpql = "SELECT f FROM Fornada f WHERE f.id = :id";
        TypedQuery<Fornada> query = em.createQuery(jpql, Fornada.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    private List<Fornada> buscarFornadasPorDataJPQL(Date data) {
        String jpql = "SELECT f FROM Fornada f WHERE f.dataFornada = :data";
        TypedQuery<Fornada> query = em.createQuery(jpql, Fornada.class);
        query.setParameter("data", data);
        return query.getResultList();
    }

    private Long contarFornadasDaPadariaJPQL(String nomePadaria) {
        String jpql = "SELECT COUNT(f) FROM Fornada f WHERE f.padaria.nome = :nome";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("nome", nomePadaria);
        return query.getSingleResult();
    }
    
    @Test
    public void testEncontrarFornadaDoDataSet() {
        logger.info("--- Executando testEncontrarFornadaDoDataSet ---");
        Fornada fornada = buscarFornadaDaPadariaJPQL("Padaria do Melhor Teste");
        assertNotNull(fornada, "Deveria existir uma fornada para a Padaria do Melhor Teste");
        assertEquals("Padaria do Melhor Teste", fornada.getPadaria().getNome());
    }

    @Test
    public void testPersistirFornada() {
        logger.info("--- Executando testPersistirFornada ---");
        Padaria padariaExistente = buscarPadariaPorNomeJPQL("Padaria do Melhor Teste");
        
        Fornada novaFornada = new Fornada();
        novaFornada.setDataFornada(new Date());
        novaFornada.setHoraInicio(new Date());
        novaFornada.setPadaria(padariaExistente);

        em.persist(novaFornada); 
        em.flush();

        assertNotNull(novaFornada.getId());
        
        em.clear();
        Fornada fornadaDoBanco = em.find(Fornada.class, novaFornada.getId());
        assertEquals(padariaExistente.getId(), fornadaDoBanco.getPadaria().getId());
    }

    @Test
    public void testAtualizarFornadaGerenciada() {
        logger.info("--- Executando testAtualizarFornadaGerenciada ---");
        Fornada fornada = em.find(Fornada.class, 1L);
        
        fornada.setDataFornada(new java.util.Date()); 
        
        em.flush();
        em.clear();

        Fornada atualizada = em.find(Fornada.class, 1L);
        assertNotNull(atualizada);
    }

    @Test
    public void testRemoverFornada() {
        logger.info("--- Executando testRemoverFornada ---");
        Fornada fornada = buscarFornadaDaPadariaJPQL("Padaria do Melhor Teste");
        em.remove(fornada);
        em.flush();
        
        Fornada apagada = buscarFornadaDaPadariaJPQL("Padaria do Melhor Teste");
        assertNull(apagada);
    }
    
    @Test
    public void testBuscaPorDataJPQL() {
        logger.info("--- Teste JPQL: Busca por Data ---");
        
        LocalDate localDate = LocalDate.of(2025, 11, 9);
        Date dataAlvo = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<Fornada> lista = buscarFornadasPorDataJPQL(dataAlvo);
        
        assertEquals(3, lista.size(), "Deveria achar as 3 fornadas do dataset");
        logger.info("Encontradas {} fornadas na data {}", lista.size(), dataAlvo);
    }

    @Test
    public void testContarFornadasJPQL() {
        logger.info("--- Teste JPQL: Count ---");
        
        Long qtd = contarFornadasDaPadariaJPQL("Padaria do Melhor Teste DOIS");
        
        assertEquals(2L, qtd);
        logger.info("A Padaria 2 fez {} fornadas.", qtd);
    }
    
    @Test
    public void testEqualsAndHashCode() {
        logger.info("--- Executando testEqualsAndHashCode ---");
        Fornada f1 = buscarFornadaPorIdJPQL(2);
        Fornada f2 = buscarFornadaPorIdJPQL(3);
        Fornada f3 = buscarFornadaPorIdJPQL(2);
                
        assertFalse(f1.equals(f2));
        assertTrue(f1.equals(f3));
        assertEquals(f1.hashCode(), f3.hashCode());
    }
}