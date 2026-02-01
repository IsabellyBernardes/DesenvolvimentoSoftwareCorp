package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaoTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(PaoTest.class);
    
    // MÉTODOS AUXILIARES ORIGINAIS
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

    // CRUD e Lógicas Antigas

    @Test
    public void testEncontrarPaoDoDataSetEIngredientes() {
        logger.info("--- Executando testEncontrarPaoDoDataSetEIngredientes ---");
        Pao pao = buscarPaoPorNome("Pão de Queijo");
        assertNotNull(pao);
        assertEquals(2, pao.getIngredientes().size());
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

        assertNotNull(novoPao.getId());
        em.clear(); 
        Pao paoDoBanco = em.find(Pao.class, novoPao.getId());
        assertEquals(1, paoDoBanco.getIngredientes().size());
    }

    @Test
    public void testAtualizarPaoComMerge() {
        logger.info("--- Executando testAtualizarPaoComMerge ---");
        Pao pao = buscarPaoPorNome("Pão Integral");
        Long idOriginal = pao.getId();
        em.clear();
        pao.setPreco(8.50); 
        em.merge(pao);
        em.flush();
        em.clear();
        Pao paoAtualizado = em.find(Pao.class, idOriginal);
        assertEquals(8.50, paoAtualizado.getPreco());
    }

    @Test
    public void testAtualizarPaoAdicionarIngrediente() {
        logger.info("--- Executando testAtualizarPaoAdicionarIngrediente ---");
        Pao pao = buscarPaoPorNome("Pão de Queijo");
        Long idOriginal = pao.getId();
        Ingrediente sal = new Ingrediente();
        sal.setNome("Sal");
        em.persist(sal);
        pao.getIngredientes().add(sal); 
        em.flush();
        em.clear();
        Pao paoAtualizado = em.find(Pao.class, idOriginal);
        assertEquals(3, paoAtualizado.getIngredientes().size());
    }

    @Test
    public void testRemoverPaoMantendoIngredientes() {
        logger.info("--- Executando testRemoverPaoMantendoIngredientes ---");
        Pao pao = buscarPaoPorNome("Pão Integral");
        Long idIngrediente = pao.getIngredientes().get(0).getId();
        em.remove(pao); 
        em.flush();
        em.clear();
        Ingrediente ingredienteSobrevivente = em.find(Ingrediente.class, idIngrediente);
        assertNotNull(ingredienteSobrevivente);
    }
    
    @Test
    public void testRemoverPaoApagaItensDePedido() {
        logger.info("--- Executando testRemoverPaoApagaItensDePedido ---");
        Pao pao = buscarPaoPorNome("Pão Integral");
        ItemPedido itemAntes = buscarItemPorNomeDoPao("Pão Integral");
        Long idItem = itemAntes.getId();
        em.remove(pao); 
        em.flush();
        em.clear();
        assertNull(em.find(ItemPedido.class, idItem));
    }

    @Test
    public void testEqualsAndHashCode() {
        Pao p1 = buscarPaoPorId(2);
        Pao p2 = buscarPaoPorId(3);
        Pao p3 = buscarPaoPorId(2);
        assertFalse(p1.equals(p2));
        assertTrue(p1.equals(p3));
        assertEquals(p1.hashCode(), p3.hashCode());
    }
}