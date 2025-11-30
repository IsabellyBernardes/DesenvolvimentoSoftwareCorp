package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ItemPedidoTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(ItemPedidoTest.class);


    private ItemPedido buscarItemPorNomeDoPao(String nomePao) {
        String jpql = "SELECT i FROM ItemPedido i WHERE i.pao.nomePao = :nomePao";
        TypedQuery<ItemPedido> query = em.createQuery(jpql, ItemPedido.class);
        query.setParameter("nomePao", nomePao);
        return query.getResultList().stream().findFirst().orElse(null);
    }
    
    private ItemPedido buscarItemPorId(int id) {
        String jpql = "SELECT i FROM ItemPedido i WHERE i.id = :id";
        TypedQuery<ItemPedido> query = em.createQuery(jpql, ItemPedido.class);
        query.setParameter("id", id);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    private Pedido buscarPedidoPorValor(Double valor) {
        String jpql = "SELECT p FROM Pedido p WHERE p.valorTotal = :valor";
        return em.createQuery(jpql, Pedido.class)
                 .setParameter("valor", valor)
                 .getSingleResult();
    }

    private Pao buscarPaoPorNome(String nome) {
        String jpql = "SELECT p FROM Pao p WHERE p.nomePao = :nome";
        return em.createQuery(jpql, Pao.class)
                 .setParameter("nome", nome)
                 .getSingleResult();
    }
    
    private Fornada buscarFornadaQualquer() {
        return em.createQuery("SELECT f FROM Fornada f", Fornada.class)
                 .getResultList().get(0);
    }


    @Test
    public void testEncontrarItemPedidoDoDataSet() {
        logger.info("--- Executando testEncontrarItemPedidoDoDataSet ---");

        ItemPedido item = buscarItemPorNomeDoPao("Pão Integral");
        assertNotNull(item, "Deveria existir um item com Pão Integral");

        logger.info("ItemPedido encontrado: ID={}", item.getId());

        assertEquals(10, item.getQuantidade());

        assertNotNull(item.getPedido());
        assertEquals(70.00, item.getPedido().getValorTotal());

        assertNotNull(item.getPao());
        assertEquals("Pão Integral", item.getPao().getNomePao());

        assertNotNull(item.getFornada());
        
        logger.info("Item validado: {} unidades de {}", 
            item.getQuantidade(), item.getPao().getNomePao());
    }

    @Test
    public void testPersistirItemPedido() {
        logger.info("--- Executando testPersistirItemPedido ---");

        Pedido pedidoExistente = buscarPedidoPorValor(70.00);
        Pao paoExistente = buscarPaoPorNome("Pão de Queijo");
        Fornada fornadaExistente = buscarFornadaQualquer();

        assertNotNull(pedidoExistente);
        assertNotNull(paoExistente);
        assertNotNull(fornadaExistente);

        ItemPedido novoItem = new ItemPedido();
        novoItem.setQuantidade(20);

        novoItem.setPedido(pedidoExistente);
        novoItem.setPao(paoExistente);
        novoItem.setFornada(fornadaExistente);

        em.persist(novoItem);
        em.flush();

        assertNotNull(novoItem.getId());
        assertTrue(novoItem.getId() > 0);
        
        ItemPedido itemDoDataset = buscarItemPorNomeDoPao("Pão Integral");
        assertNotEquals(itemDoDataset.getId(), novoItem.getId());

        logger.info("Novo ItemPedido persistido com ID={}", novoItem.getId());
    }

    @Test
    public void testAtualizarItemPedidoComMerge() {
        logger.info("--- Executando testAtualizarItemPedidoComMerge ---");

        ItemPedido item = buscarItemPorNomeDoPao("Pão Integral");
        assertNotNull(item);
        Long idOriginal = item.getId();

        em.clear();
        
        item.setQuantidade(50); 
        
        em.merge(item);
        
        em.flush();
        em.clear();
        
        ItemPedido itemDoBanco = em.find(ItemPedido.class, idOriginal);
        assertEquals(50, itemDoBanco.getQuantidade());
        
        logger.info("Quantidade do Item atualizada para 50.");
    }

    @Test
    public void testRemoverItemPedido() {
        logger.info("--- Executando testRemoverItemPedido ---");

        ItemPedido item = buscarItemPorNomeDoPao("Pão de Queijo");
        assertNotNull(item);
        
        Long idPedido = item.getPedido().getId();
        Long idPao = item.getPao().getId();

        em.remove(item);
        
        em.flush();
        em.clear();
        
        ItemPedido itemApagado = buscarItemPorNomeDoPao("Pão de Queijo");
        assertNull(itemApagado, "O item de Pão de Queijo deveria ter sido removido");
        
        assertNotNull(em.find(Pedido.class, idPedido), "O Pedido não deveria ser apagado");
        assertNotNull(em.find(Pao.class, idPao), "O Pão não deveria ser apagado");
        
        logger.info("ItemPedido removido com sucesso, pais intactos.");
    }
    
    @Test
    public void testEqualsAndHashCode() {
        logger.info("--- Executando testEqualsAndHashCode ---");

        ItemPedido ip1 = buscarItemPorId(2);
        ItemPedido ip2 = buscarItemPorId(3);
        ItemPedido ip3 = buscarItemPorId(2);

        assertFalse(ip1.equals(ip2), "Os objetos não devem ser iguais");
        assertTrue(ip1.equals(ip3), "Os objetos devem ser iguais");
        assertEquals(ip1.hashCode(), ip3.hashCode(), "Hashcodes devem ser iguais");
    }
}