package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Date;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import java.util.List;

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
    
    private Fornada buscarFornadaPorId(int id) {
        String jpql = "SELECT f FROM Fornada f WHERE f.id = :id";
        TypedQuery<Fornada> query = em.createQuery(jpql, Fornada.class);
        query.setParameter("id", id);
        return query.getResultList().stream().findFirst().orElse(null);
    }
    
    private List<Fornada> buscarFornadasPorPadariaDinamico(String nomePadaria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fornada> query = cb.createQuery(Fornada.class);
        Root<Fornada> root = query.from(Fornada.class);

        if (nomePadaria != null) {
            // JOIN: root.join("padaria") navega para a tabela Padaria
            Join<Fornada, Padaria> joinPadaria = root.join("padaria");
            
            // Compara o nome na tabela unida
            query.where(cb.like(joinPadaria.get("nome"), "%" + nomePadaria + "%"));
        }

        return em.createQuery(query).getResultList();
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
    public void testBuscaDinamicaComCriteria() {
        logger.info("--- Executando testBuscaDinamicaComCriteria (Fornada) ---");

        // Cenário A: Fornadas da "Padaria do Melhor Teste" (Exato) -> ID 1 (tem 1 fornada)
        // Nota: O LIKE pega também a "Teste 2" e "Teste 3", então precisamos ser específicos
        // Vamos filtrar pela String única da padaria 1 que não tem nas outras.
        // Mas como os nomes são muito parecidos, o ideal é filtrar pelo final.
        
        // Vamos buscar fornadas da "Padaria do Melhor Teste 2" (tem 2 fornadas no dataset)
        List<Fornada> fornadasPadaria2 = buscarFornadasPorPadariaDinamico("Teste 2");
        assertEquals(2, fornadasPadaria2.size(), "Padaria 2 deveria ter 2 fornadas");

        // Cenário B: Busca geral "Padaria" (Todas as 3 fornadas de todas as padarias)
        List<Fornada> todas = buscarFornadasPorPadariaDinamico("Padaria");
        assertEquals(3, todas.size());

        // Cenário C: Padaria inexistente
        List<Fornada> nenhuma = buscarFornadasPorPadariaDinamico("Padaria Fantasma");
        assertTrue(nenhuma.isEmpty());

        logger.info("Teste Criteria Fornada (com JOIN) OK.");
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
    
    @Test
    public void testEqualsAndHashCode() {
        logger.info("--- Executando testEqualsAndHashCode ---");

        Fornada f1 = buscarFornadaPorId(2);
        Fornada f2 = buscarFornadaPorId(3);
        Fornada f3 = buscarFornadaPorId(2);
                
        assertFalse(f1.equals(f2), "Os objetos não devem ser iguais");
        assertTrue(f1.equals(f3), "Os objetos devem ser iguais");
        assertEquals(f1.hashCode(), f3.hashCode(), "Hashcodes devem ser iguais");
    }
}