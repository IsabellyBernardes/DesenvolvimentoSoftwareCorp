package ifpe.paokentyn.domain;

import ifpe.paokentyn.util.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemPedidoTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeAll
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("DSC");
    }

    @AfterAll
    public static void tearDownClass() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    public void setUp() {
        // Carrega o dataset.xml ANTES de cada teste
        DbUnitUtil.insertData();
        
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
    }

    @AfterEach
    public void tearDown() {
        if (et != null && et.isActive()) {
            et.commit();
        }
        if (em != null) {
            em.close();
        }
    }

    // --- Nossos testes começam aqui ---

    @Test
    public void testEncontrarItemPedidoDoDataSet() {
        System.out.println("--- Executando testEncontrarItemPedidoDoDataSet ---");

        // 1. Busca o ItemPedido ID 701 (do seu dataset.xml)
        ItemPedido item = em.find(ItemPedido.class, 701L); 

        // 2. Verifica os dados dele
        assertNotNull(item, "ItemPedido 701 deveria existir no dataset");
        assertEquals(10, item.getQuantidade());

        // 3. VERIFICA TODOS OS RELACIONAMENTOS @ManyToOne
        
        // Conexão com Pedido
        assertNotNull(item.getPedido(), "O Pedido do item não deveria ser nulo");
        assertEquals(601L, item.getPedido().getId());
        assertEquals(70.00, item.getPedido().getValorTotal());

        // Conexão com Pao
        assertNotNull(item.getPao(), "O Pão do item não deveria ser nulo");
        assertEquals(301L, item.getPao().getId());
        assertEquals("Pão Integral", item.getPao().getNomePao());
        
        // Conexão com Fornada
        assertNotNull(item.getFornada(), "A Fornada do item não deveria ser nula");
        assertEquals(401L, item.getFornada().getId());
        
        System.out.println("Encontrado: " + item.getQuantidade() + "x " + item.getPao().getNomePao());
    }

    @Test
    public void testPersistirItemPedido() {
        System.out.println("--- Executando testPersistirItemPedido ---");

        // 1. PRECISAMOS DAS 3 ENTIDADES-PAI
        // Buscamos as entidades que o DBUnit inseriu
        Pedido pedidoExistente = em.find(Pedido.class, 601L);
        Pao paoExistente = em.find(Pao.class, 302L); // Pão de Queijo
        Fornada fornadaExistente = em.find(Fornada.class, 401L);

        // Verificações rápidas para garantir que o setup funcionou
        assertNotNull(pedidoExistente, "Pedido 601 (do dataset) não foi encontrado");
        assertNotNull(paoExistente, "Pão 302 (do dataset) não foi encontrado");
        assertNotNull(fornadaExistente, "Fornada 401 (do dataset) não foi encontrada");

        // 2. Criamos o novo ItemPedido
        ItemPedido novoItem = new ItemPedido();
        novoItem.setQuantidade(20); // 20 pães de queijo

        // 3. Associamos o item às 3 entidades-pai
        novoItem.setPedido(pedidoExistente);
        novoItem.setPao(paoExistente);
        novoItem.setFornada(fornadaExistente);

        // 4. Persistimos o novo item
        em.persist(novoItem); 
        em.flush(); // Força o INSERT

        // 5. Verificamos se ele foi salvo corretamente
        assertNotNull(novoItem.getId(), "ID do novo item não pode ser nulo");
        assertTrue(novoItem.getId() > 0, "ID deve ser positivo");
        assertNotEquals(701L, novoItem.getId(), "ID não deve ser o mesmo do dataset");
        assertNotEquals(702L, novoItem.getId());

        System.out.println("Persistido: Novo item com ID: " + novoItem.getId());
    }
}