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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemPedidoTest {

    private static final Logger logger = LoggerFactory.getLogger(ItemPedidoTest.class);

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeAll
    public static void setUpClass() {
        logger.info("Inicializando EntityManagerFactory...");
        emf = Persistence.createEntityManagerFactory("DSC");
        logger.info("EntityManagerFactory inicializado.");
    }

    @AfterAll
    public static void tearDownClass() {
        logger.info("Finalizando EntityManagerFactory...");
        if (emf != null) {
            emf.close();
        }
        logger.info("EntityManagerFactory fechado.");
    }

    @BeforeEach
    public void setUp() {
        logger.info("Carregando dataset XML via DbUnit...");
        DbUnitUtil.insertData();
        
        logger.info("Criando EntityManager e iniciando transação...");
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
        logger.info("Transação iniciada.");
    }

    @AfterEach
    public void tearDown() {
        logger.info("Finalizando teste → commit e fechamento do EntityManager...");
        
        if (et != null && et.isActive()) {
            et.commit();
            logger.info("Transação commitada.");
        }
        if (em != null) {
            em.close();
            logger.info("EntityManager fechado.");
        }
    }

    // --- TESTES ---

    @Test
    public void testEncontrarItemPedidoDoDataSet() {
        logger.info("--- Executando testEncontrarItemPedidoDoDataSet ---");

        ItemPedido item = em.find(ItemPedido.class, 701L);

        logger.info("ItemPedido buscado: {}", item);

        assertNotNull(item, "ItemPedido 701 deveria existir no dataset");
        assertEquals(10, item.getQuantidade());

        logger.info("Verificando relacionamentos...");

        // Pedido
        assertNotNull(item.getPedido());
        logger.info("Pedido encontrado: id={}, total={}", 
            item.getPedido().getId(), item.getPedido().getValorTotal());

        assertEquals(601L, item.getPedido().getId());
        assertEquals(70.00, item.getPedido().getValorTotal());

        // Pão
        assertNotNull(item.getPao());
        logger.info("Pão encontrado: id={}, nome={}", 
            item.getPao().getId(), item.getPao().getNomePao());

        assertEquals(301L, item.getPao().getId());
        assertEquals("Pão Integral", item.getPao().getNomePao());

        // Fornada
        assertNotNull(item.getFornada());
        logger.info("Fornada encontrada: id={}", item.getFornada().getId());

        assertEquals(401L, item.getFornada().getId());

        logger.info("ItemPedido encontrado com sucesso: {}x {}", 
            item.getQuantidade(), item.getPao().getNomePao());
    }

    @Test
    public void testPersistirItemPedido() {
        logger.info("--- Executando testPersistirItemPedido ---");

        logger.info("Buscando entidades relacionadas no dataset...");

        Pedido pedidoExistente = em.find(Pedido.class, 601L);
        Pao paoExistente = em.find(Pao.class, 302L);
        Fornada fornadaExistente = em.find(Fornada.class, 401L);

        logger.info("Pedido encontrado: {}", pedidoExistente);
        logger.info("Pão encontrado: {}", paoExistente);
        logger.info("Fornada encontrada: {}", fornadaExistente);

        assertNotNull(pedidoExistente);
        assertNotNull(paoExistente);
        assertNotNull(fornadaExistente);

        ItemPedido novoItem = new ItemPedido();
        novoItem.setQuantidade(20);

        logger.info("Associando ItemPedido às entidades pai...");

        novoItem.setPedido(pedidoExistente);
        novoItem.setPao(paoExistente);
        novoItem.setFornada(fornadaExistente);

        logger.info("Persistindo novo ItemPedido...");
        em.persist(novoItem);
        em.flush();

        logger.info("Novo ItemPedido persistido com ID={}", novoItem.getId());

        assertNotNull(novoItem.getId());
        assertTrue(novoItem.getId() > 0);
        assertNotEquals(701L, novoItem.getId());
        assertNotEquals(702L, novoItem.getId());
    }
}
