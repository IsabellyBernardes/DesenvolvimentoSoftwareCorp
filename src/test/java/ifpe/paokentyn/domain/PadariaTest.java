package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;

public class PadariaTest extends GenericTest {

    private final static String CNPJ_NOVO_TESTE = "11223344000199"; 
    private final static String CNPJ_DATASET = "99887766000199"; 


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
    
    private List<Padaria> buscarPadariasDinamico(String parteNome, String cep) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Padaria> query = cb.createQuery(Padaria.class);
        Root<Padaria> root = query.from(Padaria.class);
        List<Predicate> condicoes = new ArrayList<>();

        if (parteNome != null) {
            condicoes.add(cb.like(cb.lower(root.get("nome")), "%" + parteNome.toLowerCase() + "%"));
        }
        if (cep != null) {
            condicoes.add(cb.equal(root.get("cep"), cep));
        }

        query.where(cb.and(condicoes.toArray(new Predicate[0])));
        return em.createQuery(query).getResultList();
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
    public void testBuscaDinamicaComCriteria() {
        logger.info("--- Executando testBuscaDinamicaComCriteria (Padaria) ---");

        // Cenário A: Filtrar por CEP comum (55555555)
        // Deve trazer Padaria 2 e Padaria 3
        List<Padaria> filiais = buscarPadariasDinamico(null, "55555555");
        assertEquals(2, filiais.size());
        
        // Cenário B: Filtrar por Nome Único ("Teste 3")
        List<Padaria> padaria3 = buscarPadariasDinamico("Teste 3", null);
        assertEquals(1, padaria3.size());
        assertEquals("Padaria do Melhor Teste 3", padaria3.get(0).getNome());

        // Cenário C: Filtrar por Nome Comum ("Melhor Teste")
        // Todas as 3 padarias têm esse texto no nome
        List<Padaria> todas = buscarPadariasDinamico("Melhor Teste", null);
        assertEquals(3, todas.size());

        // Cenário D: Combinação Nome + CEP (Padaria 2)
        List<Padaria> padaria2 = buscarPadariasDinamico("Teste 2", "55555555");
        assertEquals(1, padaria2.size());

        logger.info("Teste Criteria Padaria OK.");
    }
    
    @Test
    public void testAtualizarPadariaGerenciada() {
        logger.info("--- Executando testAtualizarPadariaGerenciada (Sem Merge) ---");

        Padaria padaria = buscarPadariaPorNome("Padaria do Melhor Teste");
        assertNotNull(padaria);
        Long idOriginal = padaria.getId();
        
        String cepAntigo = padaria.getCep();
        String novoCep = "50000-999";

        padaria.setCep(novoCep);

        em.flush(); 
        em.clear();

        Padaria padariaAtualizada = em.find(Padaria.class, idOriginal);
        assertEquals(novoCep, padariaAtualizada.getCep());
        assertNotEquals(cepAntigo, padariaAtualizada.getCep());
        
        logger.info("CEP atualizado automaticamente via Dirty Checking.");
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