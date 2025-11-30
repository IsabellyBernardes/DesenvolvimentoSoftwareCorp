package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaoTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(PaoTest.class);

    private Pao buscarPaoPorNome(String nome) {
        String jpql = "SELECT p FROM Pao p WHERE p.nomePao = :nome";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }

    private Pao buscarPaoPorId(int id) {
        String jpql = "SELECT p FROM Pao p WHERE p.id = :id";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    private ItemPedido buscarItemPorNomeDoPao(String nomePao) {
        String jpql = "SELECT i FROM ItemPedido i WHERE i.pao.nomePao = :nomePao";
        TypedQuery<ItemPedido> query = em.createQuery(jpql, ItemPedido.class);
        query.setParameter("nomePao", nomePao);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    @Test
    public void testEncontrarPaoDoDataSetEIngredientes() {
        logger.info("--- Executando testEncontrarPaoDoDataSetEIngredientes ---");

        Pao pao = buscarPaoPorNome("Pão de Queijo");

        assertNotNull(pao, "Pão de Queijo deveria existir no dataset");
        assertEquals("Pão de Queijo", pao.getNomePao());
        assertEquals(3.00, pao.getPreco());

        assertNotNull(pao.getIngredientes(), "A lista de ingredientes não deveria ser nula");
        assertEquals(2, pao.getIngredientes().size(), "Pão de Queijo deve ter 2 ingredientes");

        boolean achouOvos = pao.getIngredientes().stream()
                .anyMatch(ing -> ing.getNome().equals("Ovos"));
        boolean achouPolvilho = pao.getIngredientes().stream()
                .anyMatch(ing -> ing.getNome().equals("Polvilho"));

        assertTrue(achouOvos, "Deveria ter encontrado Ovos na lista");
        assertTrue(achouPolvilho, "Deveria ter encontrado Polvilho na lista");

        logger.info("Encontrado: {} (com {} ingredientes)", pao.getNomePao(), pao.getIngredientes().size());
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

        logger.info("Persistido: {} com ID: {}", novoPao.getNomePao(), novoPao.getId());
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

        boolean achouSal = paoAtualizado.getIngredientes().stream()
                .anyMatch(ing -> ing.getNome().equals("Sal"));
        assertTrue(achouSal, "Sal deveria estar na lista");

        logger.info("Pão atualizado com sucesso.");
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

        logger.info("Pão removido, mas ingrediente {} preservado.", ingredienteSobrevivente.getNome());
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

        logger.info("Sucesso: Ao apagar o Pão, o histórico de vendas foi apagado.");
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
