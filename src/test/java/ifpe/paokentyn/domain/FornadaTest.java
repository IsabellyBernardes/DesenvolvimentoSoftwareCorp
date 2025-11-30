package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Date;

public class FornadaTest extends GenericTest {

    // --- (JPQL) ---

    private Padaria buscarPadariaPorNome(String nome) {
        String jpql = "SELECT p FROM Padaria p WHERE p.nome = :nome";
        TypedQuery<Padaria> query = em.createQuery(jpql, Padaria.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }

    private Fornada buscarFornadaDaPadaria(String nomePadaria) {
        String jpql = "SELECT f FROM Fornada f WHERE f.padaria.nome = :nomePadaria";
        TypedQuery<Fornada> query = em.createQuery(jpql, Fornada.class);
        query.setParameter("nomePadaria", nomePadaria);
        return query.getResultList().stream().findFirst().orElse(null);
    }


    @Test
    public void testEncontrarFornadaDoDataSet() {
        logger.info("--- Executando testEncontrarFornadaDoDataSet ---");

        Fornada fornada = buscarFornadaDaPadaria("Padaria do Melhor Teste");

        assertNotNull(fornada, "Deveria existir uma fornada para a Padaria do Melhor Teste");
        assertNotNull(fornada.getDataFornada());
        
        assertEquals("Padaria do Melhor Teste", fornada.getPadaria().getNome());

        logger.info("Encontrada fornada (ID={}) do dia: {}", fornada.getId(), fornada.getDataFornada());
    }

    @Test
    public void testPersistirFornada() {
        logger.info("--- Executando testPersistirFornada ---");

        Padaria padariaExistente = buscarPadariaPorNome("Padaria do Melhor Teste");
        assertNotNull(padariaExistente, "Padaria do dataset não encontrada");

        Fornada novaFornada = new Fornada();
        novaFornada.setDataFornada(new Date());
        novaFornada.setHoraInicio(new Date());
        novaFornada.setPadaria(padariaExistente);

        em.persist(novaFornada); 
        em.flush();

        assertNotNull(novaFornada.getId());
        assertTrue(novaFornada.getId() > 0);

        em.clear();
        Fornada fornadaDoBanco = em.find(Fornada.class, novaFornada.getId());
        
        assertNotNull(fornadaDoBanco);
        assertEquals(padariaExistente.getId(), fornadaDoBanco.getPadaria().getId());

        logger.info("Nova fornada persistida com sucesso. ID Gerado: {}", novaFornada.getId());
    }

    @Test
    public void testAtualizarFornadaGerenciada() {
        logger.info("--- Executando testAtualizarFornadaGerenciada (Sem Merge) ---");

        Fornada fornada = buscarFornadaDaPadaria("Padaria do Melhor Teste");
        assertNotNull(fornada);
        Long idOriginal = fornada.getId(); 
        Date dataAntiga = fornada.getDataFornada();
        
        Date novaData = new Date(); 
        fornada.setDataFornada(novaData);
        
        em.flush();
        em.clear();
        
        Fornada fornadaAtualizada = em.find(Fornada.class, idOriginal);
        assertNotEquals(dataAntiga, fornadaAtualizada.getDataFornada());
        
        logger.info("Data da fornada atualizada automaticamente.");
    }

    @Test
    public void testAtualizarFornadaComMerge() {
        logger.info("--- Executando testAtualizarFornadaComMerge ---");

        Fornada fornada = buscarFornadaDaPadaria("Padaria do Melhor Teste");
        assertNotNull(fornada);
        Long idOriginal = fornada.getId();
        
        em.clear(); 
        
        Date novaHora = new Date();
        fornada.setHoraInicio(novaHora);
        
        em.merge(fornada); 
        
        em.flush();
        em.clear();
        
        Fornada fornadaVerificada = em.find(Fornada.class, idOriginal);
        assertNotNull(fornadaVerificada.getHoraInicio());
        
        logger.info("Fornada atualizada via merge.");
    }

    @Test
    public void testRemoverFornada() {
        logger.info("--- Executando testRemoverFornada ---");

        Fornada fornada = buscarFornadaDaPadaria("Padaria do Melhor Teste");
        assertNotNull(fornada);
        
        em.remove(fornada);
        
        em.flush();
        em.clear();

        Fornada fornadaApagada = buscarFornadaDaPadaria("Padaria do Melhor Teste");
        assertNull(fornadaApagada, "A fornada deveria ter sido removida");
        
        logger.info("Fornada removida com sucesso.");
    }
}