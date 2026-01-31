package ifpe.paokentyn.domain;

import ifpe.paokentyn.domain.GenericTest;
import ifpe.paokentyn.domain.Ingrediente;
import ifpe.paokentyn.domain.ItemPedido;
import ifpe.paokentyn.domain.Pao;
import jakarta.persistence.TypedQuery;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaoTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(PaoTest.class);
    
    // MÉTODOS AUXILIARES ORIGINAIS (JPQL)

    private Pao buscarPaoPorNome(String nome) {
        String jpql = "SELECT p FROM Pao p WHERE p.nomePao = :nome";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }

    private Pao buscarPaoPorId(int id) {
        String jpql = "SELECT p FROM Pao p WHERE p.id = :id";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("id", (long) id);
        return query.getSingleResult();
    }

    private ItemPedido buscarItemPorNomeDoPao(String nomePao) {
        String jpql = "SELECT i FROM ItemPedido i WHERE i.pao.nomePao = :nomePao";
        TypedQuery<ItemPedido> query = em.createQuery(jpql, ItemPedido.class);
        query.setParameter("nomePao", nomePao);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    // JQPL NOVOS

    private Pao buscarPaoComIngredientesFetchJPQL(Long id) {
        // 1. JOIN FETCH
        String jpql = "SELECT p FROM Pao p JOIN FETCH p.ingredientes WHERE p.id = :id";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    private List<Object[]> listarPaesComMuitosIngredientesJPQL(Long qtdMinima) {
        // 2. GROUP BY + HAVING
        String jpql = "SELECT p.nomePao, COUNT(i) FROM Pao p JOIN p.ingredientes i " +
                      "GROUP BY p.nomePao HAVING COUNT(i) >= :qtd";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("qtd", qtdMinima);
        return query.getResultList();
    }

    private List<Pao> buscarResumoPaoJPQL(Double precoMaximo) {
        // 3. NEW 
        String jpql = "SELECT NEW ifpe.paokentyn.domain.Pao(p.nomePao, p.preco) " + 
                      "FROM Pao p WHERE p.preco < :max";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("max", precoMaximo);
        return query.getResultList();
    }

    private List<Pao> buscarPaesComImagemJPQL() {
        // 4. NOT NULL
        String jpql = "SELECT p FROM Pao p WHERE p.imagem IS NOT NULL";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        return query.getResultList();
    }

    private List<Pao> buscarPaesPorFaixaPrecoJPQL(Double min, Double max) {
        // 5. BETWEEN
        String jpql = "SELECT p FROM Pao p WHERE p.preco BETWEEN :min AND :max ORDER BY p.preco ASC";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("min", min);
        query.setParameter("max", max);
        return query.getResultList();
    }

    //  NOVOS TESTES JPQL 
    
    @Test
    public void testBuscarPaoComIngredientesFetch() {
        logger.info("--- JPQL: JOIN FETCH ---");
        // ID 2 (Pão de Queijo) tem ingredientes no dataset
        Pao pao = buscarPaoComIngredientesFetchJPQL(2L);
        assertNotNull(pao);
        assertFalse(pao.getIngredientes().isEmpty());
        logger.info("Pão carregado com Fetch: {}", pao.getNomePao());
    }

    @Test
    public void testAgrupamentoIngredientes() {
        logger.info("--- JPQL: GROUP BY + HAVING ---");
        List<Object[]> lista = listarPaesComMuitosIngredientesJPQL(2L);
        assertFalse(lista.isEmpty());
        logger.info("Pão agrupado: {} - Qtd: {}", lista.get(0)[0], lista.get(0)[1]);
    }

    @Test
    public void testRetornoComNew() {
        logger.info("--- JPQL: NEW ---");
        List<Pao> lista = buscarResumoPaoJPQL(20.0);
        assertFalse(lista.isEmpty());
        assertNull(lista.get(0).getId()); 
        assertNotNull(lista.get(0).getNomePao());
        logger.info("Resumo recuperado: {}", lista.get(0).getNomePao());
    }

    @Test
    public void testImagemNaoNula() {
        logger.info("--- JPQL: IS NOT NULL ---");
        List<Pao> lista = buscarPaesComImagemJPQL();
        assertNotNull(lista); 
    }

    @Test
    public void testFaixaDePreco() {
        logger.info("--- JPQL: BETWEEN ---");
        List<Pao> lista = buscarPaesPorFaixaPrecoJPQL(1.0, 10.0);
        assertFalse(lista.isEmpty());
        logger.info("Pão na faixa: {}", lista.get(0).getNomePao());
    }

    // CRUD e LOgicas Antigas

    @Test
    public void testEncontrarPaoDoDataSetEIngredientes() {
        logger.info("--- Executando testEncontrarPaoDoDataSetEIngredientes ---");
        Pao pao = buscarPaoPorNome("Pão de Queijo");
        assertNotNull(pao, "Pão de Queijo deveria existir no dataset");
        assertEquals("Pão de Queijo", pao.getNomePao());
        assertEquals(3.00, pao.getPreco());
        assertNotNull(pao.getIngredientes(), "A lista de ingredientes não deveria ser nula");
        assertEquals(2, pao.getIngredientes().size(), "Pão de Queijo deve ter 2 ingredientes");
        
        boolean achouOvos = pao.getIngredientes().stream().anyMatch(ing -> ing.getNome().equals("Ovos"));
        boolean achouPolvilho = pao.getIngredientes().stream().anyMatch(ing -> ing.getNome().equals("Polvilho"));
        
        assertTrue(achouOvos, "Deveria ter encontrado Ovos na lista");
        assertTrue(achouPolvilho, "Deveria ter encontrado Polvilho na lista");
    }

    @Test
    public void testPersistirPaoComNovoIngrediente() {
        logger.info("--- Executando testPersistirPaoComNovoIngrediente ---");
        Ingrediente novoIngrediente = new Ingrediente();
        novoIngrediente.setNome("Fermento Biológico");
        
        Pao novoPao = new Pao();
        novoPao.setNomePao("Pão Francês");
        novoPao.setPreco(0.75);
        
        novoPao.setIngredientes(List.of(novoIngrediente));

        em.persist(novoIngrediente); 
        em.persist(novoPao);
        em.flush(); 

        assertNotNull(novoPao.getId(), "ID do novo pão não pode ser nulo");
        assertNotNull(novoIngrediente.getId(), "ID do novo ingrediente não pode ser nulo");
        
        Pao paoDoDataset = buscarPaoPorNome("Pão de Queijo");
        assertNotEquals(paoDoDataset.getId(), novoPao.getId());

        em.clear(); 
        
        Pao paoDoBanco = em.find(Pao.class, novoPao.getId());
        assertEquals(1, paoDoBanco.getIngredientes().size());
        assertEquals("Fermento Biológico", paoDoBanco.getIngredientes().get(0).getNome());
    }

    @Test
    public void testAtualizarPaoComMerge() {
        logger.info("--- Executando testAtualizarPaoComMerge ---");
        Pao pao = buscarPaoPorNome("Pão Integral");
        assertNotNull(pao);
        Long idOriginal = pao.getId();
        Double precoAntigo = pao.getPreco();

        em.clear();
        pao.setPreco(8.50); 
        em.merge(pao);
        em.flush();
        em.clear();

        Pao paoAtualizado = em.find(Pao.class, idOriginal);
        assertEquals(8.50, paoAtualizado.getPreco());
        assertNotEquals(precoAntigo, paoAtualizado.getPreco());
    }

    @Test
    public void testAtualizarPaoAdicionarIngrediente() {
        logger.info("--- Executando testAtualizarPaoAdicionarIngrediente ---");
        Pao pao = buscarPaoPorNome("Pão de Queijo");
        assertNotNull(pao);
        Long idOriginal = pao.getId();
        
        Ingrediente sal = new Ingrediente();
        sal.setNome("Sal");
        em.persist(sal);

        pao.getIngredientes().add(sal); 
        em.flush();
        em.clear();
        
        Pao paoAtualizado = em.find(Pao.class, idOriginal);
        assertEquals(3, paoAtualizado.getIngredientes().size(), "Agora deve ter 3 ingredientes");
        assertTrue(paoAtualizado.getIngredientes().stream().anyMatch(ing -> ing.getNome().equals("Sal")));
    }

    @Test
    public void testRemoverPaoMantendoIngredientes() {
        logger.info("--- Executando testRemoverPaoMantendoIngredientes ---");
        Pao pao = buscarPaoPorNome("Pão Integral");
        assertNotNull(pao);
        
        Ingrediente ing = pao.getIngredientes().get(0);
        Long idIngrediente = ing.getId();
        
        em.remove(pao); 
        em.flush();
        em.clear();
        
        String jpqlCheck = "SELECT p FROM Pao p WHERE p.nomePao = :nome";
        List<Pao> lista = em.createQuery(jpqlCheck, Pao.class)
                            .setParameter("nome", "Pão Integral")
                            .getResultList();
        assertTrue(lista.isEmpty());
        
        Ingrediente ingredienteSobrevivente = em.find(Ingrediente.class, idIngrediente);
        assertNotNull(ingredienteSobrevivente, "O ingrediente NÃO deveria ser apagado");
    }
    
    @Test
    public void testRemoverPaoApagaItensDePedido() {
        logger.info("--- Executando testRemoverPaoApagaItensDePedido ---");
        Pao pao = buscarPaoPorNome("Pão Integral");
        ItemPedido itemAntes = buscarItemPorNomeDoPao("Pão Integral");
        assertNotNull(itemAntes, "Deveria existir um item de pedido com Pão Integral");
        Long idItem = itemAntes.getId();

        em.remove(pao); 
        em.flush();
        em.clear();

        ItemPedido itemDepois = em.find(ItemPedido.class, idItem);
        assertNull(itemDepois, "O ItemPedido deveria ter sido apagado em cascata!");
    }

    @Test
    public void testEqualsAndHashCode() {
        logger.info("--- Executando testEqualsAndHashCode ---");
        Pao p1 = buscarPaoPorId(2);
        Pao p2 = buscarPaoPorId(3);
        Pao p3 = buscarPaoPorId(2);
        assertFalse(p1.equals(p2), "Os objetos não devem ser iguais");
        assertTrue(p1.equals(p3), "Os objetos devem ser iguais");
        assertEquals(p1.hashCode(), p3.hashCode(), "Hashcodes devem ser iguais");
    }
}